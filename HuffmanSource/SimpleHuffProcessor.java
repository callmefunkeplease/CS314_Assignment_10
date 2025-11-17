/*  Student information for assignment:
 *
 *  On our honor, Funke and <NAME2>, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 Funke
 *  UTEID: jof369
 *  email address: jof369@eid.utexas.edu
 *  Grader name: Karnika Choudhury
 *
 *  Student 2
 *  UTEID: psp782
 *  email address: psp782@my.utexas.edu
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private HuffmanTree huffTree;
    private Integer format;
    private int compressedSize;

    /**
     * Preprocess data so that compression is possible ---
     * count characters/create tree/store state so that
     * a subsequent call to compress will work. The InputStream
     * is <em>not</em> a BitInputStream, so wrap it int one as needed.
     * 
     * @param in           is the stream which could be subsequently compressed
     * @param headerFormat a constant from IHuffProcessor that determines what kind
     *                     of header to use, standard count format, standard tree
     *                     format, or possibly some format added in the future.
     * @return number of bits saved by compression or some other measure
     *         Note, to determine the number of
     *         bits saved, the number of bits written includes
     *         ALL bits that will be written including the
     *         magic number, the header format number, the header to
     *         reproduce the tree, AND the actual data.
     * @throws IOException if an error occurs while reading from the input file.
     */
    public int preprocessCompress(InputStream in, int headerFormat) throws IOException {
        BitInputStream bitStream;
        if (!(in instanceof BitInputStream)) {
            bitStream = new BitInputStream(in);
        } else {
            bitStream = (BitInputStream) in;
        }
        format = headerFormat;
        huffTree = new HuffmanTree(bitStream);
        // TODO is this all good? Do the math and double check l8r
        compressedSize = BITS_PER_INT * 2;
        if (headerFormat == STORE_COUNTS) {
            compressedSize += ALPH_SIZE * BITS_PER_WORD;
        } else if (headerFormat == STORE_TREE) {
            compressedSize += huffTree.numBitsToCompressTree();
        }
        compressedSize+=huffTree.getCompressionBitCount();
        return huffTree.originalBitCount() - compressedSize;
    }

    /**
     * Compresses input to output, where the same InputStream has
     * previously been pre-processed via <code>preprocessCompress</code>
     * storing state used by this call.
     * <br>
     * pre: <code>preprocessCompress</code> must be called before this method
     * 
     * @param in    is the stream being compressed (NOT a BitInputStream)
     * @param out   is bound to a file/stream to which bits are written
     *              for the compressed file (not a BitOutputStream)
     * @param force if this is true create the output file even if it is larger than
     *              the input file.
     *              If this is false do not create the output file if it is larger
     *              than the input file.
     * @return the number of bits written.
     * @throws IOException if an error occurs while reading from the input file or
     *                     writing to the output file.
     */
    public int compress(InputStream in, OutputStream out, boolean force) throws IOException {
        if (format.equals(0)) {
            throw new IllegalStateException("Must call preprocessCompress before compress");
        }
        // Step 1: Output stream out the header we made
        BitOutputStream bitOut = new BitOutputStream(out);
        bitOut.writeBits(BITS_PER_INT, MAGIC_NUMBER);
        bitOut.writeBits(BITS_PER_INT, format);
        if (format == STORE_COUNTS) {
            int[] frequencies = huffTree.getFrequencies();
            for (int bite = 0; bite < ALPH_SIZE; bite++) {
                bitOut.writeBits(BITS_PER_INT, frequencies[bite]);
            }
        } else if (format == STORE_TREE) {
            huffTree.writeTree(bitOut);
        }

        // Step 2: Output stream out the compression

        HashMap<Integer, BitList> compMap = huffTree.getCompressionMap();
        int bite = in.read();
        while (bite != -1) {
            bitOut.writeBits(compMap.get(bite).getSize(), compMap.get(bite).toInt());
            bite = in.read();
        }
        bitOut.writeBits(compMap.get(PSEUDO_EOF).getSize(), compMap.get(PSEUDO_EOF).toInt());

        bitOut.close();
        
        return compressedSize;
    }

    /**
     * Uncompress a previously compressed stream in, writing the
     * uncompressed bits/data to out.
     * 
     * @param in  is the previously compressed data (not a BitInputStream)
     * @param out is the uncompressed file/stream
     * @return the number of bits written to the uncompressed file/stream
     * @throws IOException if an error occurs while reading from the input file or
     *                     writing to the output file.
     */
    public int uncompress(InputStream in, OutputStream out) throws IOException {
        throw new IOException("uncompress not implemented");
        // return 0;
    }

    public void setViewer(IHuffViewer viewer) {
        myViewer = viewer;
    }

    private void showString(String s) {
        if (myViewer != null) {
            myViewer.update(s);
        }
    }
}
