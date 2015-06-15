package org.incava.analysis;

import java.util.List;
import org.incava.ijdk.io.IO;

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

    /**
     * Returns a string representing the given reference, consistent with the
     * format of the Report subclass.
     */
    public String getDifference(FileDiff fdiff) {
        StringBuilder sb = new StringBuilder();

        printDiffSummary(sb, fdiff);
        printLines(sb, fdiff);
        
        return sb.toString();
    }

    protected void printDiffSummary(StringBuilder sb, FileDiff fdiff) {
        sb.append(fdiff.toDiffSummaryString());
        sb.append(' ');
        sb.append(fdiff.getMessage());
        sb.append(EOLN);
    }

    protected abstract void printLines(StringBuilder sb, FileDiff fdiff);

    public void printFrom(StringBuilder sb, FileDiff fdiff) {
    }

    public void printTo(StringBuilder sb, FileDiff fdiff) {
    }
}
