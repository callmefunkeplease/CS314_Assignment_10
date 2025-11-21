/*  Student information for assignment:
 *
 *  On our honor, Funke and Pranav, this programming assignment is our own work
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
import java.util.Iterator;
import java.util.LinkedList;

public class PriorityQueue314<E extends Comparable> {
    private static final int PERMUTATIONS_OF_A_BYTE = 256;

    private LinkedList<E> queue;

    /**
     * Initializes the inner container
     */
    public PriorityQueue314(){
        queue=new LinkedList<>();
    } 

    /**
     * Adds a new value to the PriorityQueue
     * 
     * @param newVal  the value being added
     * @return a boolean true value
     */
    public boolean add(E newVal) {
        //TODO GO TO OFFICE HOURS AND ASK ABOUT THIS UNSAFE CAST???
        if(size()==0||newVal.compareTo(queue.getLast())>=0){
            queue.addLast(newVal);
            return true;
        }
        Iterator<E> it=queue.iterator();
        int index=0;
        E cur=it.next();
        while(it.hasNext()&&newVal.compareTo(cur)>=0){
            index++;
            cur=it.next();
        }
        queue.add(index, newVal);
        return true;
    }

    /**
     * Returns a glimpse of the first element in the PriorityQueue
     * 
     * @return the first value in the PriorityQueue
     */
    public E peek(){
        return queue.getFirst();
    }

    /**
     * Prints out the PriorityQueue
     * 
     * @return String representation of the PriorityQueue
     */
    public String toString(){
        return queue.toString();
    }

    /**
     * Removes the first value from the PriorityQueue and returns it
     * 
     * @return the first value in the PriorityQueue
     */
    public E dequeue() {
        if(size()==0){
            return null;
        }
        return queue.removeFirst();
    }

    /**
     * Return the size of the PriorityQueue
     * 
     * @return an int value of the number of elements in the PriorityQueue
     */
    public int size(){
        return queue.size();
    }
}