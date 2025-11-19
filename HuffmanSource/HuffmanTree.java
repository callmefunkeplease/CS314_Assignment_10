
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


    //TODO comment the logic behind this method and implementation
    public HuffmanTree(BitInputStream bitIn) throws IOException {
        int sizeLeft = bitIn.readBits(BITS_PER_INT);
        int curBit = bitIn.readBits(1);
        if (curBit == 0) {
            // root has 2 kids
            root = new TreeNode(-1, -1);
            Stack<TreeNode> myNodes = new Stack<>();
            myNodes.push(root);
            sizeLeft--;
            while (sizeLeft != 0) {
                curBit = bitIn.readBits(1);
                TreeNode nextNode;
                if (curBit == 1) {
                    // next is leaf
                    nextNode = new TreeNode(bitIn.readBits(BITS_PER_WORD + 1), -1);
                    sizeLeft -= (BITS_PER_WORD + 2);
                } else {
                    // next is a branch
                    nextNode = new TreeNode(-1, -1);
                    sizeLeft--;
                }
                while (!giveChild(myNodes.peek(), nextNode)) {
                    myNodes.pop();
                }
                if (curBit != 1) {
                    myNodes.push(nextNode);
                }
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
        } else if (parent.getRight() == null) {
            parent.setRight(child);
        } else {
            return false;
        }
        return true;
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
            System.out.println("1 " + toBinary(curNode.getValue()) + " ");
        } else {
            out.writeBits(1, 0);
            System.out.print("0 ");
            writeTree(out, curNode.getLeft());
            writeTree(out, curNode.getRight());
        }
    }

    private String toBinary(int in) {
        String r = "";
        for (int i = 0; i < 9; i++) {
            r = in % 2 + r;
            in /= 2;
        }
        return r;
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
                    bitsWritten+=BITS_PER_WORD;
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
        //this happens in the 1/8 chance the PEOF *is* the last thing
        return bitsWritten;
    }
}
