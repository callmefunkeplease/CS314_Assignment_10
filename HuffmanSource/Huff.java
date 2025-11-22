import javax.swing.UIManager;

/**
 * When this program is run, it opens a Java Swing panel, in which a user can
 * use the buttons in the top left to check what their compression would compress
 * to, compress a file using Huffman style compression, or uncompress a file that
 * was previously compressed in the Huffman style. 
 * 
 * The options tab allows a user to decide weather or not it will force compression
 * even if compressing does more harm than good. This setting is on by default
 * 
 * The Header format tab allows different formats of Huffman compression. By default
 * it is set to Count format, however in every single possible scenario, tree format
 * is more efficient.
 */
public class Huff {

    /*
     * Create an interface to interact with a
     *  Huffman processor.
     */
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            System.out.println("Unable to set look at feel to local settings. " +
                    "Continuing with default Java look and feel.");
        }
        // To use a Graphical User Interface to perform Huffman operations.
        // Comment out the following line and uncomment the line after to use a TextHuffViewer.
        IHuffViewer sv = new GUIHuffViewer("Huffman Compression");
        // IHuffViewer sv = new TextHuffViewer();
        IHuffProcessor proc = new SimpleHuffProcessor();
        sv.setModel(proc);    
        if (sv instanceof TextHuffViewer) {
            ((TextHuffViewer) sv).start();
        }
    }
}
