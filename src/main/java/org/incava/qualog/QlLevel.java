package org.incava.qualog;

import java.io.*;
import java.util.*;


/**
 * <p>Represents a logging/output level. Is essentially a wrapper around an
 * Integer.</p>
 */
class QlLevel implements Comparable
{
    private Integer level = null;
    
    public QlLevel(int level) {
        this(new Integer(level));
    }

    public QlLevel(Integer level) {
        this.level = level;
    }

    public int compareTo(Object other) {
        if (other instanceof QlLevel) {
            QlLevel qother = (QlLevel)other;
            return this.level.compareTo(qother.level);
        }
        else {
            return -1;
        }
    }

    public boolean equals(Object other) {
        return compareTo(other) == 0;
    }

    public String toString() {
        return level.toString();
    }

}
