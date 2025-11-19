
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

public class HuffmanTree implements IHuffConstants {
    TreeNode root;
    private int[] frequencies;
    private int numLeafs;
    private int numBits;

    public HuffmanTree(int[] frequencies) throws IOException {
        // PriorityQueue314 is a queue of treeNodes. You can only add treeNodes to it.
        PriorityQueue314<TreeNode> queue = new PriorityQueue314<>();
        for (int bite = 0; bite < ALPH_SIZE; bite++) {
            if (frequencies[bite] != 0) {
                queue.add(new TreeNode(bite, frequencies[bite]));
            }
        }
        // This is the PEOF
        queue.add(new TreeNode(ALPH_SIZE, 1));
        numLeafs = queue.size();
        // cause each leaf is 10 bits.
        numBits = numLeafs * (BITS_PER_WORD + 2);

        while (queue.size() > 1) {
            queue.add(new TreeNode(queue.dequeue(), -1, queue.dequeue()));
            // every time this queue adds something, it's now a new internal node
            numBits++;
        }

        root = queue.dequeue();
    }

    public HuffmanTree(BitInputStream bitIn) throws IOException {
        int curBit = bitIn.readBits(1);
        if (curBit == 0) {
            // root has 2 kids
            int sizeLeft = bitIn.readBits(BITS_PER_INT);
            root = new TreeNode(-1, -1);
            Stack<TreeNode> myNodes = new Stack<>();
            myNodes.push(root);
            while (sizeLeft != 0) {
                if (curBit == 1) {
                    // next is leaf
                    TreeNode child = new TreeNode(bitIn.read(), -1);
                    while(!giveChild(myNodes.peek(), child)){
                        myNodes.pop();
                    }
                    sizeLeft-=(BITS_PER_WORD+2);
                }else{
                    //next is a branch
                    TreeNode nextNode=new TreeNode(-1, -1);
                    giveChild(myNodes.peek(), nextNode);
                    myNodes.push(nextNode);
                    sizeLeft--;
                }
                curBit=bitIn.readBits(1);
            }
        } else {
            // root is a leaf
            root = new TreeNode(null, bitIn.readBits(BITS_PER_WORD), null);
            // We should end the program here
        }
    }

    private boolean giveChild(TreeNode parent, TreeNode child) {
        if (parent.getLeft() == null) {
            parent.setLeft(child);
            return true;
        } else if (parent.getRight() == null) {
            parent.setRight(child);
            return true;
        } else {
            return false;
        }
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

    public void writeTree(BitOutputStream out) {
        out.writeBits(BITS_PER_INT, numBits);
        writeTree(out, root);
    }

    private void writeTree(BitOutputStream out, TreeNode curNode) {
        if (curNode.isLeaf()) {
            // says "this a leaf, boss!"
            out.writeBits(1, 1);
            // says what the data actually is.
            out.writeBits(BITS_PER_WORD + 1, curNode.getValue());
        } else {
            out.writeBits(1, 0);
            writeTree(out, curNode.getLeft());
            writeTree(out, curNode.getRight());
        }
    }

    public HashMap<Integer, BitList> getCompressionMap() {
        HashMap<Integer, BitList> compressionMap = new HashMap<>();
        BitList b = new BitList();
        fillMap(compressionMap, root, b);
        return compressionMap;
    }

    private void fillMap(HashMap<Integer, BitList> mapToFill, TreeNode curNode, BitList curList) {
        if (curNode.isLeaf()) {
            mapToFill.put(curNode.getValue(), new BitList(curList));
        } else {
            curList.addRight(0);
            fillMap(mapToFill, curNode.getLeft(), curList);
            curList.removeFront();
            curList.addRight(1);
            fillMap(mapToFill, curNode.getRight(), curList);
            curList.removeFront();
        }
    }

    public int uncompress(BitInputStream bitIn, BitOutputStream bitOut) throws IOException {
        TreeNode curNode = root;
        int curBit = bitIn.readBits(1);
        int bitsWritten = 0;
        while (curBit != -1) {
            if (curNode.isLeaf()) {
                if (curNode.getValue() != PSEUDO_EOF) {
                    bitOut.writeBits(BITS_PER_WORD, curNode.getValue());
                    curNode = root;
                } else {
                    return bitsWritten;
                }
            } else {
                if (curBit == 0) {
                    curNode = curNode.getLeft();
                } else {
                    curNode = curNode.getRight();
                }
                curBit = bitIn.readBits(1);
            }
        }
        // TODO delete that system.out
        System.out.println("YO YO YO AN ERROR HAPPENED WHEN WRITING BITS I NEVER SAW A PEOF! FIX THIS IMMEDIATELY");
        return bitsWritten;
    }
}
