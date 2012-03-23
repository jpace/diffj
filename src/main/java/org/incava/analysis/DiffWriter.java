package org.incava.analysis;

import java.util.List;
import org.incava.ijdk.io.IO;
import org.incava.ijdk.lang.*;

/**
 * Writes differences. Actually returns the differences as strings. Writing is
 * left to the invoker.
 */
public abstract class DiffWriter {
    /**
     * The end-of-line character/sequence for this OS.
     */
    protected final static String EOLN = IO.EOLN;

    /**
     * The from-contents, separated by new lines, which are included at the end
     * of each string.
     */
    protected final List<String> fromContents;

    /**
     * The to-contents, separated by new lines, which are included at the end
     * of each string.
     */
    protected final List<String> toContents;

    public DiffWriter(List<String> fromContents, List<String> toContents) {
        this.fromContents = fromContents;
        this.toContents = toContents;
    }

    public String toString(int x, int y) {
        StringBuffer buf = new StringBuffer();
        buf.append(x);
        if (x != y) {
            buf.append(",").append(y);
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
        sb.append(toString(ref.getFirstLocation().getStart().getLine(),  ref.getFirstLocation().getEnd().getLine()));
        sb.append(ref.getType());
        sb.append(toString(ref.getSecondLocation().getStart().getLine(), ref.getSecondLocation().getEnd().getLine()));
        sb.append(' ');
        sb.append(ref.getMessage());
        sb.append(EOLN);
    }

    protected abstract void printLines(StringBuilder sb, FileDiff ref);
}
