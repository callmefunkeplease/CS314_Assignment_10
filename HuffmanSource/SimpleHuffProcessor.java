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

import javax.swing.JOptionPane;

public class SimpleHuffProcessor implements IHuffProcessor {

    private IHuffViewer myViewer;
    private HuffmanTree huffTree;
    private Integer format;
    private int compressedSize;
    private int[] frequencies;

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
        huffTree = new HuffmanTree(getFrequencies(bitStream));
        compressedSize = BITS_PER_INT * 2;
        if (headerFormat == STORE_COUNTS) {
            compressedSize += ALPH_SIZE * BITS_PER_WORD;
        } else if (headerFormat == STORE_TREE) {
            compressedSize += BITS_PER_INT + huffTree.numBitsToCompressTree();
        }
        compressedSize += huffTree.getCompressionBitCount();
        compressedSize += (BITS_PER_WORD - (compressedSize % BITS_PER_WORD));
        return huffTree.originalBitCount() - compressedSize;
    }

    private int[] getFrequencies(BitInputStream input) throws IOException {
        frequencies = new int[ALPH_SIZE];
        int bite = input.read();
        while (bite != -1) {
            frequencies[bite]++;
            bite = input.read();
        }
        return frequencies;
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
        // TODO if !force then see if this thing actually expands instead of compress
        // TODO and not force.
        if (format.equals(0)) {
            throw new IllegalStateException("Must call preprocessCompress before compress");
        }
        // Step 1: Output stream out the header we made
        BitOutputStream bitOut = new BitOutputStream(out);
        bitOut.writeBits(BITS_PER_INT, MAGIC_NUMBER);
        bitOut.writeBits(BITS_PER_INT, format);
        if (format == STORE_COUNTS) {
            for (int bite = 0; bite < ALPH_SIZE; bite++) {
                bitOut.writeBits(BITS_PER_INT, frequencies[bite]);
            }
        } else if (format == STORE_TREE) {
            huffTree.writeTree(bitOut);
        }

        // Step 2: Output stream out the compression
        HashMap<Integer, BitList> compMap = huffTree.getCompressionMap();
        BitInputStream bitIn = new BitInputStream(in);
        int bite = bitIn.read();
        while (bite != -1) {
            bitOut.writeBits(compMap.get(bite).getSize(), compMap.get(bite).toInt());
            bite = bitIn.read();
        }
        bitOut.writeBits(compMap.get(PSEUDO_EOF).getSize(), compMap.get(PSEUDO_EOF).toInt());

        bitOut.close();
        bitIn.close();

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
        BitInputStream bitIn = new BitInputStream(in);
        BitOutputStream bitOut = new BitOutputStream(out);

        if (bitIn.readBits(BITS_PER_INT) != MAGIC_NUMBER) {
            myViewer.showError("Caution, never found huffman file identifier."
                    + " High chance to result in gibberish.");
        }

        int format = bitIn.readBits(BITS_PER_INT);
        HuffmanTree sisterTree;
        if (format == STORE_COUNTS) {
            frequencies = new int[ALPH_SIZE];
            for (int bite = 0; bite < ALPH_SIZE; bite++) {
                frequencies[bite] = bitIn.readBits(BITS_PER_INT);
            }
            sisterTree = new HuffmanTree(frequencies);
        } else if (format == STORE_TREE) {
            sisterTree = new HuffmanTree(bitIn);
        } else {
            // because it's not a recognized format so it will write an empty file
            return 0;
        }
        return sisterTree.uncompress(bitIn, bitOut);
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
