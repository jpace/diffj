package org.incava.ijdk.text;

/**
 * Location of text in a string (or file), denoted by both relative position
 * (0-indexed point in a stream) and a line and column number (both of which are
 * 1-indexed).
 */
public class TextLocation extends Location {
    public final static int UNDEFINED = -317;
    
    private final int position;

    public TextLocation(int position, int line, int column) {
        super(line, column);        
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public String toString() {
        return "[position: " + this.position + ", line: " + getLine() + ", column: " + getColumn() + "]";
    }

    public boolean equals(Object obj) {
        return obj instanceof TextLocation && equals((TextLocation)obj);
    }

    public boolean equals(TextLocation other) {
        return compareTo(other) == 0;
    }

    public int compareTo(TextLocation other) {
        int cmp = new Integer(this.position).compareTo(other.getPosition());
        if (cmp == 0) {
            cmp = super.compareTo(other);
        }

        return cmp;
    }
    
    public int hashCode() {
        int hash = super.hashCode();
        return hash * 31 + position;
    }
}
