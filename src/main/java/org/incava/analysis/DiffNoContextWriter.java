package org.incava.analysis;

import java.awt.Point;
import java.util.List;

/**
 * Writes differences. Actually returns the differences as strings. Writing is
 * left to the invoker.
 */
public class DiffNoContextWriter extends DiffWriter {
    public DiffNoContextWriter(List<String> fromContents, List<String> toContents) {
        super(fromContents, toContents);
    }

    protected void printFrom(StringBuilder sb, FileDiff ref) {
        Point del = new Point(ref.getFirstStart().x,  ref.getFirstEnd().x);
        printLines(sb, del, "<", fromContents);
    }

    protected void printTo(StringBuilder sb, FileDiff ref) {
        Point add = new Point(ref.getSecondStart().x, ref.getSecondEnd().x);
        printLines(sb, add, ">", toContents);
    }

    protected void printLines(StringBuilder sb, FileDiff ref) {
        ref.print(this, sb);
        sb.append(EOLN);
    }

    protected void printLines(StringBuilder sb, Point pt, String ind, List<String> lines) {
        for (int lnum = pt.x; lnum <= pt.y; ++lnum) {
            sb.append(ind + " " + lines.get(lnum - 1));
            sb.append(EOLN);
        }
    }
}
