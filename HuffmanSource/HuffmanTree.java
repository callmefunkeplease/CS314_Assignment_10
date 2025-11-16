
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class HuffmanTree {
    private final static int PERMUTATIONS_OF_A_BYTE=256;
    TreeNode root;
    private final static int BITS_PER_BYTE=8;

    public HuffmanTree(BitInputStream input) throws IOException {
        // PriorityQueue314 is a queue of treeNodes. You can only add treeNodes to it.
        PriorityQueue314<TreeNode> queue = new PriorityQueue314<>();
        int[] frequencies=getFrequencies(input);
        for(int bite=0;bite<PERMUTATIONS_OF_A_BYTE;bite++){
            if(frequencies[bite]!=0){
                queue.add(new TreeNode(bite, frequencies[bite]));
            }
        }
        //This is the PEOF
        queue.add(new TreeNode(PERMUTATIONS_OF_A_BYTE, 1));
        
        while(queue.size()>1){
            queue.add(new TreeNode(queue.dequeue(), -1, queue.dequeue()));
        }

        root=queue.dequeue();
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

    public int getBitCount(){
        return getBitCount(root, 0);
    }

    private int getBitCount(TreeNode curNode, int depth){
        if(curNode.isLeaf()){
            return depth*curNode.getFrequency();
        }
        return getBitCount(curNode.getLeft(), depth+1)+getBitCount(curNode.getRight(), depth+1);
    }

    public int originalBitCount(){
        return BITS_PER_BYTE*root.getFrequency();
    }

    private void printTree(TreeNode n, String spaces) {
        if (n != null) {
            printTree(n.getRight(), spaces + "  ");
            System.out.println(spaces + (char)n.getValue());
            printTree(n.getLeft(), spaces + "  ");
        }
    }

}
