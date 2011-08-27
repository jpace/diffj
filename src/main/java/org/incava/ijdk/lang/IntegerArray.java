package org.incava.ijdk.lang;

import java.io.*;
import java.util.*;


/**
 * An array that allows insertion at any point, not within the set size.
 */
public class IntegerArray {
    private TreeMap<Integer, Integer> data;

    public IntegerArray() {
        data = new TreeMap<Integer, Integer>();
    }

    public Integer get(Integer index) {
        return data.get(index);
    }

    public void set(Integer index, Integer value) {
        data.put(index, value);
    }

    public void remove(Integer index) {
        data.remove(index);
    }

    public int size() {
        return data.size() == 0 ? 0 : 1 + lastKey().intValue();
    }

    public Integer lastKey() {
        return lastIndex();
    }

    public Integer lastIndex() {
        return data.size() == 0 ? null : data.lastKey();
    }

    public Integer lastValue() {
        return get(lastKey());
    }

    public String toString() {
        return data.toString();
    }

    public void add(Integer value) {
        int last = size();
        set(last, value);
    }

    public Integer[] toArray() {
        Integer[] ary = new Integer[size()];
        for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
            ary[entry.getKey()] = entry.getValue();
        }
        return ary;
    }

}
