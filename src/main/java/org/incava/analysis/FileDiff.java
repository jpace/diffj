package org.incava.analysis;

import java.awt.Point;
import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.lang.ObjectExt;

/**
 * A message, associated with a file by a starting and ending position.
 */
public class FileDiff implements Comparable<FileDiff> {
    public enum Type {
        ADDED("a"),
        DELETED("d"),
        CHANGED("c");

        private final String str;

        Type(String str) {
            this.str = str;
        }

        public String toString() {
            return str;
        }
    }

    public static Point toBeginPoint(Token t) {
        return t == null ? null : new Point(t.beginLine, t.beginColumn);
    }
    
    public static Point toEndPoint(Token t) {
        return t == null ? null : new Point(t.endLine, t.endColumn);
    }

    /**
     * The message for this reference. This should be only one line, because it
     * is used in single-line reports.
     */
    public String message;
   
    /**
     * The line and colum where the reference starts in the first file.
     */
    public Point firstStart;

    /**
     * The line and column where the reference ends in the first file.
     */
    public Point firstEnd;

    /**
     * The line and colum where the reference starts in the second file.
     */
    public Point secondStart;

    /**
     * The line and column where the reference ends in the second file.
     */
    public Point secondEnd;

    public Type type;

    /**
     * Creates a reference from a message and begin and end positions.
     *
     * @param type        What type this reference is.
     * @param message     The message applying to this reference.
     * @param firstStart  In the from-file, where the reference begins.
     * @param firstEnd    In the from-file, where the reference ends.
     * @param secondStart In the to-file, where the reference begins.
     * @param secondEnd   In the to-file, where the reference ends.
     */
    public FileDiff(Type type, String message, Point firstStart, Point firstEnd, Point secondStart, Point secondEnd) {
        this.type        = type;
        this.message     = message;
        this.firstStart  = firstStart;
        this.firstEnd    = firstEnd;
        this.secondStart = secondStart;
        this.secondEnd   = secondEnd;
    }

    /**
     * Creates a reference from a message and two tokens, one in each file.
     *
     * @param type    What type this reference is.     
     * @param message The message applying to this reference.
     * @param a       The token in the first file.
     * @param b       The token in the second file.
     */
    public FileDiff(Type type, String message, Token a, Token b) {
        this(type, message, toBeginPoint(a), toEndPoint(a), toBeginPoint(b), toEndPoint(b));
    }

    /**
     * Creates a reference from a message and two beginning and ending tokens.
     */
    public FileDiff(Type type, String message, Token a0, Token a1, Token b0, Token b1) {
        this(type, message, toBeginPoint(a0), toEndPoint(a1), toBeginPoint(b0), toEndPoint(b1));
    }

    /**
     * Compares this reference to another. FileDiffs are sorted in order by
     * their beginning locations, then their end locations.
     *
     * @param obj The reference to compare this to.
     * @return -1, 0, or 1, for less than, equivalent to, or greater than.
     */
    public int compareTo(FileDiff other) {
        if (this == other) {
            return 0;
        }
        else {
            Point[][] pts = new Point[][] {
                { firstStart,  other.firstStart  },
                { secondStart, other.secondStart },
                { firstEnd,    other.firstEnd    },
                { secondEnd,   other.secondEnd   },
            };
            for (int i = 0; i < pts.length; ++i) {
                // tr.Ace.log("pts[" + i + "][" + 0 + "]: " + pts[i][0] + "; pts[" + i + "][" + 0 + "]: " + pts[i][1]);
                if (pts[i][0] == null) {
                    if (pts[i][1] != null) {
                        return 1;
                    }
                }
                else if (pts[i][1] == null) {
                    return -1;
                }
                else {
                    int cmp = pts[i][0].x - pts[i][1].x;
                    if (cmp == 0) {
                        cmp = pts[i][0].y - pts[i][1].y;
                    }
                    if (cmp != 0) {
                        return cmp;
                    }
                }
            }

            int cmp = type.compareTo(other.type);
            if (cmp == 0) {
                cmp = message.compareTo(other.message);
            }
            return cmp;
        }
    }

    /**
     * Returns whether the other object is equal to this one.
     *
     * @param obj The reference to compare this to.
     * @return Whether the other reference is equal to this one.
     */
    public boolean equals(Object obj) {
        if (obj instanceof FileDiff) {
            FileDiff ref = (FileDiff)obj;
            return compareTo(ref) == 0;
        }
        else {
            return false;
        }
    }

    protected boolean equal(Point a, Point b) {
        return ObjectExt.areEqual(a, b);
    }

    /**
     * Returns "x:y".
     */
    public String toString(Point pt) {
        StringBuffer buf = new StringBuffer();
        if (pt == null) {
            buf.append("null");
        }
        else {
            buf.append(pt.x);
            buf.append(":");
            buf.append(pt.y);
        }
        return buf.toString();
    }

    /**
     * Returns "x:y .. x:y".
     */
    public String toString(Point ptOne, Point ptTwo) {
        StringBuffer buf = new StringBuffer();
        buf.append(toString(ptOne)).append(" .. ").append(toString(ptTwo));
        return buf.toString();
    }

    /**
     * Returns this reference, as a string.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        buf.append(type.toString());
        buf.append(" from: ");
        buf.append(toString(firstStart, firstEnd));
        if (secondStart != null) {
            buf.append(" to: ").append(toString(secondStart, secondEnd));
        }
        buf.append("] (").append(message).append(")");
        return buf.toString();
    }

    /**
     * Returns the line and colum where the reference starts in the first file.
     */
    public Point getFirstStart() {
        return firstStart;
    }

    /**
     * Returns the line and column where the reference ends in the first file.
     */
    public Point getFirstEnd() {
        return firstEnd;
    }

    /**
     * Returns the line and colum where the reference starts in the second file.
     */
    public Point getSecondStart() {
        return secondStart;
    }

    /**
     * Returns the line and column where the reference ends in the second file.
     */
    public Point getSecondEnd() {
        return secondEnd;
    }    

    public void print(DiffContextWriter dw, StringBuilder sb) {
    }

    public void print(DiffNoContextWriter dw, StringBuilder sb) {
    }
}
