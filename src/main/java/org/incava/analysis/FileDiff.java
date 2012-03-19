package org.incava.analysis;

import java.awt.Point;
import net.sourceforge.pmd.ast.Token;
import org.incava.ijdk.lang.ObjectExt;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;

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
    private final String message;
   
    /**
     * The line and colum where the reference starts in the first file.
     */
    private final Location firstStart;

    /**
     * The line and column where the reference ends in the first file.
     */
    private final Location firstEnd;

    /**
     * The line and colum where the reference starts in the second file.
     */
    private final Location secondStart;

    /**
     * The line and column where the reference ends in the second file.
     */
    private final Location secondEnd;

    private final Type type;

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
    // public FileDiff(Type type, String message, Location firstStart, Location firstEnd, Location secondStart, Location secondEnd) {
    //     this.type        = type;
    //     this.message     = message;
    //     this.firstStart  = pointToLocation(firstStart);
    //     this.firstEnd    = pointToLocation(firstEnd);
    //     this.secondStart = secondStart;
    //     this.secondEnd   = secondEnd;
    // }

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
        this.firstStart  = pointToLocation(firstStart);
        this.firstEnd    = pointToLocation(firstEnd);
        this.secondStart = pointToLocation(secondStart);
        this.secondEnd   = pointToLocation(secondEnd);
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
        
        Point[][] pts = new Point[][] {
            { getFirstStart(),  other.getFirstStart()  },
            { getSecondStart(), other.getSecondStart() },
            { getFirstEnd(),    other.getFirstEnd()    },
            { getSecondEnd(),   other.getSecondEnd()   },
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

    /**
     * Returns this reference, as a string.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(type.toString());
        sb.append(" from: ");
        sb.append(toString(firstStart, firstEnd));
        if (secondStart != null) {
            sb.append(" to: ").append(toString(secondStart, secondEnd));
        }
        sb.append("] (").append(message).append(")");
        return sb.toString();
    }

    /**
     * Returns the line and colum where the reference starts in the first file.
     */
    public Point getFirstStart() {
        return locationToPoint(firstStart);
    }

    /**
     * Returns the line and column where the reference ends in the first file.
     */
    public Point getFirstEnd() {
        return locationToPoint(firstEnd);
    }

    /**
     * Returns the line and colum where the reference starts in the second file.
     */
    public Point getSecondStart() {
        return locationToPoint(secondStart);
    }

    /**
     * Returns the line and column where the reference ends in the second file.
     */
    public Point getSecondEnd() {
        return locationToPoint(secondEnd);
    }    

    public void print(DiffContextWriter dw, StringBuilder sb) {
    }

    public void print(DiffNoContextWriter dw, StringBuilder sb) {
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    private static Point locationToPoint(Location loc) {
        return loc == null ? null : new Point(loc.getLine(), loc.getColumn());
    }

    private static Location pointToLocation(Point pt) {
        return pt == null ? null : new Location(pt.x, pt.y);
    }

    /**
     * Returns "x:y".
     */
    public static String toString(Location loc) {
        StringBuilder sb = new StringBuilder();
        sb.append(loc == null ? "null" : loc.toString());
        return sb.toString();
    }

    /**
     * Returns "x:y .. x:y".
     */
    public static String toString(Location a, Location b) {
        StringBuilder sb = new StringBuilder();
        sb.append(toString(a)).append(" .. ").append(toString(b));
        return sb.toString();
    }
}
