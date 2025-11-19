import java.util.Iterator;

public class BitList implements Iterable {
    private static final int BITS_PER_BYTE=8;
    private boolean[] myBits;
    private int size;

    public BitList() {
        myBits = new boolean[BITS_PER_BYTE];
        size = 0;
    }

    public BitList(BitList otherList) {
        myBits = new boolean[otherList.myBits.length];
        size = otherList.size;
        for (int i = 0; i < otherList.size; i++) {
            myBits[i] = otherList.myBits[i];
        }
    }

    public BitList(String otherList) {
        int length = BITS_PER_BYTE;
        size = otherList.length();
        while (length < size) {
            length *= 2;
        }
        myBits = new boolean[length];
        for (int i = size - 1; i >= 0; i--) {
            if (otherList.charAt(i) == '1') {
                myBits[i] = true;
            }
        }
    }

    public BitList(int input){
        myBits=new boolean[BITS_PER_BYTE];
        size=1;
        int indexVal=2;
        while(indexVal<=input){
            size++;
            indexVal*=2;
        }
        resize();
        int index=0;
        while(input!=0){
            myBits[index]=input%2==1;
            index++;
            input/=2;
        }
    }

    public int getSize(){
        return size;
    }

    public int toInt() {
        int intToReturn = 0;
        for (int i = 0; i < size; i++) {
            if (myBits[i]) {
                intToReturn += (1 << i);
            }
        }
        return intToReturn;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = size - 1; i >= 0; i--) {
            if (myBits[i]) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        return sb.toString();
    }

    public void leftShift(int amount) {
        size += amount;
        resize();
        for (int i = size - amount - 1; i >= 0; i--) {
            myBits[i + amount] = myBits[i];
        }
    }

    public void rightShift(int amount) {
        size -= amount;
        if (size < 0) {
            size = 0;
            myBits = new boolean[BITS_PER_BYTE];
        } else {
            for (int i = amount; i < size + amount; i++) {
                myBits[i - amount] = myBits[i];
            }
        }
    }

    public void addRight(int bit) {
        leftShift(1);
        if (bit == 1) {
            myBits[0] = true;
        } else {
            myBits[0] = false;
        }
    }

    public void addLeft(int bit) {
        size++;
        resize();
        if (bit == 1) {
            myBits[size - 1] = true;
        } else {
            myBits[size - 1] = false;
        }
    }

    public void removeFront(){
        rightShift(1);
    }

    public void removeBack(){
        size--;
        if(size<0){
            size=0;
            myBits=new boolean[BITS_PER_BYTE];
        }
    }

    public Iterator iterator() {
        return new BIterator();
    }

    private void resize() {
        if (size >= myBits.length) {
            int length = myBits.length;
            while (length < size) {
                length *= 2;
            }
            boolean[] temp = new boolean[length];
            for (int i = 0; i < size; i++) {
                temp[i] = myBits[i];
            }
            myBits = temp;
        }
    }

    private void resize(BitList other) {
        if (other.myBits.length > myBits.length) {
            boolean[] temp = new boolean[other.myBits.length];
            for (int i = 0; i < size; i++) {
                temp[i] = myBits[i];
            }
            myBits = temp;
        }
    }

    private class BIterator implements Iterator<Integer> {
        int curPos = -1;

        public boolean hasNext() {
            return size > curPos + 1;
        }

        public Integer next() {
            curPos++;
            if (myBits[curPos]) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
