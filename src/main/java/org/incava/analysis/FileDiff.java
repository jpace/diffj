package org.incava.analysis;

import org.incava.ijdk.lang.ObjectExt;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.ijdk.text.Message;

/**
 * A message, associated with a file by a starting and ending position.
 */
public abstract class FileDiff implements Comparable<FileDiff> {
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

    public static LocationRange toLocationRange(Location from, Location to) {
        return from == null ? null : new LocationRange(from, to);
    }
    
    /**
     * The message for this reference. This should be only one line, because it
     * is used in single-line reports.
     */
    private final String message;
   
    /**
     * The location in the first file.
     */
    private final LocationRange firstLocation;

    /**
     * The location in the second file.
     */
    private final LocationRange secondLocation;

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
    public FileDiff(Type type, String message, Location firstStart, Location firstEnd, Location secondStart, Location secondEnd) {
        this(type, message, new LocationRange(firstStart, firstEnd), secondStart == null ? null : new LocationRange(secondStart, secondEnd));
    }

    /**
     * Creates a reference from a message and begin and end positions.
     *
     * @param type        What type this reference is.
     * @param message     The message applying to this reference.
     * @param fromLoc     The location range in the from-file.
     * @param toLoc       The location range in the to-file.
     */
    public FileDiff(Type type, String message, LocationRange fromLoc, LocationRange toLoc) {
        this.type           = type;
        this.message        = message;
        this.firstLocation  = fromLoc;
        this.secondLocation = toLoc;
    }

    /**
     * Creates a reference from a message and begin and end positions.
     *
     * @param type        What type this reference is.
     * @param fromLoc     The location range in the from-file.
     * @param toLoc       The location range in the to-file.
     * @param msg         The message applying to this reference.
     * @param params      The parameters for the message.
     */
    public FileDiff(Type type, LocationRange fromLoc, LocationRange toLoc, Message msg, Object ... params) {
        this.type           = type;
        this.message        = msg.format(params);
        this.firstLocation  = fromLoc;
        this.secondLocation = toLoc;
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

        int cmp = ObjectExt.compare(firstLocation, other.firstLocation);
        if (cmp != 0) {
            return cmp;
        }

        cmp = ObjectExt.compare(secondLocation, other.secondLocation);
        if (cmp != 0) {
            return cmp;
        }

        cmp = type.compareTo(other.type);
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
            FileDiff fdiff = (FileDiff)obj;
            return compareTo(fdiff) == 0;
        }
        else {
            return false;
        }
    }

    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Returns this reference, as a string.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(type.toString());
        sb.append(" from: ");
        sb.append(toString(firstLocation));
        if (secondLocation != null) {
            sb.append(" to: ").append(toString(secondLocation));
        }
        sb.append("] (").append(message).append(")");
        return sb.toString();
    }

    // returns "1" (if same line) or "1,3" (multiple lines)
    public String toLineString(LocationRange lr) {
        int fromLine = lr.getStart().getLine();
        int endLine = lr.getEnd().getLine();
        StringBuilder sb = new StringBuilder();
        sb.append(fromLine);
        if (fromLine != endLine) {
            sb.append(",").append(endLine);
        }
        return sb.toString();
    }

    // returns 1a,8, 3,14c4,10 ...
    public String toDiffSummaryString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toLineString(getFirstLocation()));
        sb.append(getType());
        sb.append(toLineString(getSecondLocation()));
        return sb.toString();
    }

    /**
     * Returns the location in the first file.
     */
    public LocationRange getFirstLocation() {
        return firstLocation;
    }

    /**
     * Returns the location in the second file.
     */
    public LocationRange getSecondLocation() {
        return secondLocation;
    }

    public abstract void printContext(DiffWriter dw, StringBuilder sb);

    public abstract void printNoContext(DiffWriter dw, StringBuilder sb);

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }

    public boolean isOnSameLine(LocationRange loc) {
        return getFirstLocation().getStart().getLine() == loc.getStart().getLine();
    }

    /**
     * Returns "x:y".
     */
    public static String toString(Location loc) {
        return loc == null ? "null" : loc.toString();
    }

    /**
     * Returns "x:y .. x:y".
     */
    public static String toString(Location from, Location to) {
        StringBuilder sb = new StringBuilder();
        sb.append(toString(from)).append(" .. ").append(toString(to));
        return sb.toString();
    }

    /**
     * Returns "x:y .. x:y".
     */
    public static String toString(LocationRange lr) {
        return toString(lr.getStart(), lr.getEnd());
    }
}
