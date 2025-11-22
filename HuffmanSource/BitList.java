/*  Student information for assignment:
 *
 *  On our honor, Funke and Pranav, this programming assignment is our own work
 *  and we have not provided this code to any other student.
 *
 *  Number of slip days used: 1
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

public class BitList {
    private static final int BITS_PER_BYTE = 8;
    private boolean[] myBits;
    private int size;

    /* constructs an empty bitList */
    public BitList() {
        myBits = new boolean[BITS_PER_BYTE];
        size = 0;
    }

    /*
     * makes a deep copy of an inputted BitList
     * param: otherList!=null
     */
    public BitList(BitList otherList) {
        if (otherList == null) {
            throw new IllegalArgumentException("otherList cannot be null");
        }
        myBits = new boolean[otherList.myBits.length];
        size = otherList.size;
        for (int i = 0; i < otherList.size; i++) {
            myBits[i] = otherList.myBits[i];
        }
    }

    /*
     * returns the size of the bitList
     * Return: the number of bits in this bitList
     */
    public int getSize() {
        return size;
    }

    /*
     * returns the integer representation of this bitList
     * return: this bitList if it were an int
     * caution* the number of leading zeros aren't maintained in the return value
     */
    public int toInt() {
        int intToReturn = 0;
        for (int i = 0; i < size; i++) {
            if (myBits[i]) {
                intToReturn += (1 << i);
            }
        }
        return intToReturn;
    }

    /*
     * shifts all the data in this bitList to the left
     * pre: amount>0
     * post: the size is increased by amount, the toInt() value is multiplied by
     * 2^amount, and
     * all the bits are shifted to the left by amount
     */
    public void leftShift(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("shift amount in leftShift is too small");
        }
        size += amount;
        resize();
        for (int i = size - amount - 1; i >= 0; i--) {
            myBits[i + amount] = myBits[i];
        }
    }

    /*
     * Shifts all the data to the right by amounts
     * 
     * pre: amount > 0
     * 
     * post: the size is decreased by amount, up to becoming 0, and all the bits are
     * shifted to the right, deleting the value of bits that go out of bound
     * 
     * note* calling rightShift(Integer.MAXVALUE) will result in a all the data
     * being removed and this bitList being reset as though the initial constructor
     * was called.
     */
    public void rightShift(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("shift amount in rightShift is too small");
        }
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

    /*
     * appends the inputed bit to the right of the the bitList
     * pre: bit can only == 1 or 0
     * post: the inputted bit is appended to the right of the current bitList. The
     * result of toInt() is effectively x2 + bit. Size is increased by one.
     */
    public void addRight(int bit) {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("can only add bits 1 or 0");
        }
        leftShift(1);
        if (bit == 1) {
            myBits[0] = true;
        } else {
            myBits[0] = false;
        }
    }

    /*
     * removes the bit at the front of this bitList
     * post: the bit in the ones place is removed and all the remaining bits are
     * shifted to the right by one. size is decreased by one
     */
    public void removeFront() {
        rightShift(1);
    }

    private void resize() {
        if (size >= myBits.length) {
            int length = myBits.length;
            while (length < size) {
                length *= 2;
            }
            boolean[] temp = new boolean[length];
            for (int i = 0; i < size && i < myBits.length; i++) {
                temp[i] = myBits[i];
            }
            myBits = temp;
        }
    }
}
