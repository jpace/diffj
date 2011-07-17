package org.incava.ijdk.util;

import java.util.Comparator;


/**
 * A comparator for reversed order.
 */
public class ReverseComparator<T extends Comparable<T>> implements Comparator<T> {
    /**
     * Compares o2 to o1. <code>o2</code> must implement Comparable.
     */
    public int compare(T o1, T o2) {
        return o2.compareTo(o1);
    }
    
    /**
     * Returns <code>o1.equals(o2)</code>.
     */
    public boolean equals(T o1, T o2) {
        return o1.equals(o2);
    }
}
