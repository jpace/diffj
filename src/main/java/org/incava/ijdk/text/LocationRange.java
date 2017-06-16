package org.incava.ijdk.text;

import org.incava.ijdk.lang.ObjectExt;

/**
 * A starting and ending location.
 */
public class LocationRange implements Comparable<LocationRange> {
    private final Location start;
    private final Location end;

    public LocationRange(Location start, Location end) {
        this.start = start;
        this.end = end;
    }

    public Location getStart() {
        return this.start;
    }

    public Location getEnd() {
        return this.end;
    }

    public String toString() {
        return "[" + start + " .. " + end + "]";
    }

    public boolean equals(Object obj) {
        return obj instanceof LocationRange && equals((LocationRange)obj);
    }

    public boolean equals(LocationRange other) {
        return ObjectExt.equal(other.getStart(), start) && ObjectExt.equal(other.getEnd(), end);
    }

    public int compareTo(LocationRange other) {
        int cmp = ObjectExt.compare(start, other.start);
        if (cmp == 0) {
            cmp = ObjectExt.compare(end, other.end);
        }
        return cmp;
    }

    public int hashCode() {
        return this.start.hashCode() * 17 + this.end.hashCode();
    }
}
