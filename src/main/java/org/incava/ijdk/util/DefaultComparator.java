package org.incava.ijdk.util;

import java.util.Comparator;


/**
 * Provides a default implementation for a Comparator. <code>equals</code>
 * relies on <code>compare</code> (returning true if <code>compare</code>
 * returns 0). <code>compare</code> also checks for the given class, if
 * provided.
 */
public abstract class DefaultComparator<T> implements Comparator<T> {
    private Class type;
    
    public DefaultComparator(Class type) {
        this.type = type;
    }

    public DefaultComparator() {
        this(null);
    }

    public int compare(T x, T y) {
        int cmp = 0;
        if (x == null) {
            if (y == null) {
                cmp = 0;
            }
            else {
                cmp = -1;
            }
        }
        else if (y == null) {
            cmp = 1;
        }
        else if (type == null || (x.getClass().isAssignableFrom(type) && y.getClass().isAssignableFrom(type))) {
            cmp = doCompare(x, y);
        }
        else {
            cmp = -1;
        }

//         tr.Ace.log("x", x);
//         tr.Ace.log("y", y);
//         tr.Ace.log("cmp: " + cmp);

        return cmp;
    }
                
    public boolean equals(T x, T y) {
        return compare(x, y) == 0;
    }

    public abstract int doCompare(T x, T y);
}
