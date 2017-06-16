package org.incava.ijdk.text;

import org.incava.ijdk.lang.ObjectExt;

/**
 * A range of text, denoted by starting and ending locations.
 */
public class TextRange {
    private final TextLocation start;
    private final TextLocation end;

    public TextRange(TextLocation start, TextLocation end) {
        this.start = start;
        this.end = end;
    }

    public TextLocation getStart() {
        return this.start;
    }

    public TextLocation getEnd() {
        return this.end;
    }

    public String toString() {
        return "[start: " + start + ", end: " + end + "]";
    }

    public boolean equals(Object obj) {
        return obj instanceof TextRange && equals((TextRange)obj);
    }

    public boolean equals(TextRange other) {
        return ObjectExt.equal(other.start, start) && ObjectExt.equal(other.end, end);
    }

    public int hashCode() {
        return start.hashCode() * 31 + end.hashCode();
    }
}
