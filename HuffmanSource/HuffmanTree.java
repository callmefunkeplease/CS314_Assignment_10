
import java.io.IOException;
import java.util.HashMap;

public class HuffmanTree implements IHuffConstants {
    private final static int PERMUTATIONS_OF_A_BYTE = 256;
    TreeNode root;
    private int[] frequencies;
    private int numLeafs;
    private int numBits;

    public HuffmanTree(BitInputStream input) throws IOException {
        // PriorityQueue314 is a queue of treeNodes. You can only add treeNodes to it.
        PriorityQueue314<TreeNode> queue = new PriorityQueue314<>();
        frequencies = getFrequencies(input);
        for (int bite = 0; bite < PERMUTATIONS_OF_A_BYTE; bite++) {
            if (frequencies[bite] != 0) {
                queue.add(new TreeNode(bite, frequencies[bite]));
            }
        }
        // This is the PEOF
        queue.add(new TreeNode(PERMUTATIONS_OF_A_BYTE, 1));
        numLeafs = queue.size();
        // cause each leaf is 9 bits.
        numBits = numLeafs * (BITS_PER_WORD + 1);

        while (queue.size() > 1) {
            queue.add(new TreeNode(queue.dequeue(), -1, queue.dequeue()));
            // every time this queue adds something, it's now a new internal node
            numBits++;
        }

        root = queue.dequeue();
    }

    private int[] getFrequencies(BitInputStream input) throws IOException {
        int[] frequencies = new int[PERMUTATIONS_OF_A_BYTE];
        int bite = input.read();
        while (bite != -1) {
            frequencies[bite]++;
            bite = input.read();
        }
        return frequencies;
    }

    public int[] getFrequencies() {
        return frequencies;
    }

    public int numBitsToCompressTree() {
        return numBits;
    }

    public int getCompressionBitCount() {
        return getBitCount(root, 0);
    }

    private int getBitCount(TreeNode curNode, int depth) {
        if (curNode.isLeaf()) {
            return depth * curNode.getFrequency();
        }
        return getBitCount(curNode.getLeft(), depth + 1) + getBitCount(curNode.getRight(), depth + 1);
    }

    public int originalBitCount() {
        // minus 1 for the added PEOF value
        return BITS_PER_WORD * (root.getFrequency() - 1);
    }

    private void printTree(TreeNode n, String spaces) {
        if (n != null) {
            printTree(n.getRight(), spaces + "  ");
            System.out.println(spaces + (char) n.getValue());
            printTree(n.getLeft(), spaces + "  ");
        }
    }

    public void writeTree(BitOutputStream out) {
        out.writeBits(BITS_PER_INT, numBits);
        writeTree(out, root);
    }

    private void writeTree(BitOutputStream out, TreeNode curNode) {
        if (curNode.isLeaf()) {
            // says "this a leaf, boss!"
            out.writeBits(1, 1);
            // says what the data actually is.
            out.writeBits(BITS_PER_INT + 1, curNode.getValue());
        } else {
            out.writeBits(1, 0);
            writeTree(out, curNode.getLeft());
            writeTree(out, curNode.getRight());
        }
    }

    public HashMap<Integer, BitList> getCompressionMap(){
        HashMap<Integer, BitList> compressionMap=new HashMap<>();
        BitList b=new BitList();
        fillMap(compressionMap, root, b);
        return compressionMap;
    }

    private void fillMap(HashMap<Integer, BitList> mapToFill, TreeNode curNode, BitList curList){
        if(curNode.isLeaf()){
            mapToFill.put(curNode.getValue(), new BitList(curList));
        }else{
            curList.addFront(0);
            fillMap(mapToFill, curNode.getLeft(), curList);
            curList.removeFront();
            curList.addFront(1);
            fillMap(mapToFill, curNode.getRight(), curList);
            curList.removeFront();
        }
    }

}
