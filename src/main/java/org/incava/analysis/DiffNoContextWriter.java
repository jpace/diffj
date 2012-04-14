package org.incava.analysis;

import java.util.List;
import org.incava.ijdk.text.LocationRange;

/**
 * Writes differences. Actually returns the differences as strings. Writing is
 * left to the invoker.
 */
public class DiffNoContextWriter extends DiffWriter {
    public DiffNoContextWriter(List<String> fromContents, List<String> toContents) {
        super(fromContents, toContents);
    }

    public void printFrom(StringBuilder sb, FileDiff fdiff) {
        printLines(sb, fdiff.getFirstLocation(), "<", fromContents);
    }

    public void printTo(StringBuilder sb, FileDiff fdiff) {
        printLines(sb, fdiff.getSecondLocation(), ">", toContents);
    }

    protected void printLines(StringBuilder sb, FileDiff fdiff) {
        fdiff.printNoContext(this, sb);
        sb.append(EOLN);
    }

    protected void printLines(StringBuilder sb, LocationRange loc, String ind, List<String> lines) {
        int fromLine = loc.getStart().getLine();
        int throughLine = loc.getEnd().getLine();
        for (int lnum = fromLine; lnum <= throughLine; ++lnum) {
            sb.append(ind + " " + lines.get(lnum - 1));
            sb.append(EOLN);
        }
    }
}
