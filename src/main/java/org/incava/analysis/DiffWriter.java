package org.incava.analysis;

import java.awt.Point;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.lang.*;

/**
 * Writes differences. Actually returns the differences as strings. Writing is
 * left to the invoker.
 */
public abstract class DiffWriter {
    /**
     * The end-of-line character/sequence for this OS.
     */
    protected final static String EOLN = FileExt.EOLN;

    /**
     * The from-contents, separated by new lines, which are included at the end
     * of each string.
     */
    protected final String[] fromContents;

    /**
     * The to-contents, separated by new lines, which are included at the end
     * of each string.
     */
    protected final String[] toContents;

    public DiffWriter(String[] fromContents, String[] toContents) {
        this.fromContents = fromContents;
        this.toContents = toContents;
    }

    public String toString(Point pt) {
        StringBuffer buf = new StringBuffer();
        buf.append(pt.x);
        if (pt.x != pt.y) {
            buf.append(",").append(pt.y);
        }
        return buf.toString();
    }

    /**
     * Returns a string representing the given reference, consistent with the
     * format of the Report subclass.
     */
    public String getDifference(FileDiff ref) {
        StringBuilder sb = new StringBuilder();

        printDiffSummary(sb, ref);
        printLines(sb, ref);
        
        return sb.toString();
    }

    protected void printDiffSummary(StringBuilder sb, FileDiff ref) {
        Point del = new Point(ref.firstStart.x,  ref.firstEnd.x);
        Point add = new Point(ref.secondStart.x, ref.secondEnd.x);

        FileDiff.Type ind = ref.type;

        sb.append(toString(del));
        sb.append(ind);
        sb.append(toString(add));
        sb.append(' ');
        sb.append(ref.message);
        sb.append(EOLN);
    }

    protected abstract void printLines(StringBuilder sb, FileDiff ref);
}
