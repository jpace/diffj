package org.incava.ijdk.text;

/**
 * Code location.
 */
public class Location implements Comparable<Location> {
    public final int line;    
    public final int column;
    
    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String toString() {
        return "" + line + ":" + column;
    }

    public boolean equals(Object obj) {
        return obj instanceof Location && equals((Location)obj);
    }

    public boolean equals(Location other) {
        return compareTo(other) == 0;
    }

    public int compareTo(Location other) {
        int cmp = new Integer(this.line).compareTo(other.getLine());
        if (cmp == 0) {
            cmp = new Integer(this.column).compareTo(other.getColumn());
        }
        return cmp;
    }

    public int hashCode() {
        return line * 31 + column;
    }
}
