import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class PriorityQueue314<E extends Comparable> {
    private static final int PERMUTATIONS_OF_A_BYTE = 256;

    private LinkedList<E> queue;

    //TODO ADD COMMENTS TO THIS ENTIRE CLASS PLEASE AND THANK YOU
    public PriorityQueue314(){
        queue=new LinkedList<>();
    } 

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

    public E peek(){
        return queue.getFirst();
    }

    public String toString(){
        return queue.toString();
    }

    public E dequeue() {
        if(size()==0){
            return null;
        }
        return queue.removeFirst();
    }

    public int size(){
        return queue.size();
    }
}