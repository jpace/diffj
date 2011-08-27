package org.incava.ijdk.lang;

import java.io.*;
import java.util.*;


public class Range {
    private int first;

    private int last;
    
    public Range(int first, int last) {
        this.first = first;
        this.last = last;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
        if (first > last) {
            last = first;
        }
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
        if (first > last) {
            first = last;
        }
    }

    public boolean includes(int n) {
        return n >= first && n <= last;
    }

    public Object[] lastArray() {
        return new Object[] { new Integer(first), new Integer(last) };
    }

    public boolean equals(Object obj) {
        if (obj instanceof Range) {
            Range other = (Range)obj;
            return first == other.first && last == other.last;
        }
        else {
            return false;
        }
    }

    public void expandTo(int i) {
        if (i < first) {
            first = i;
        }
        if (i > last) {
            last = i;
        }
    }

    public String toString() {
        return "[" + first + ", " + last + "]";
    }

}
