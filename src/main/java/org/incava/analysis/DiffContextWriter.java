package org.incava.analysis;

import java.util.List;
import org.incava.ijdk.io.*;
import org.incava.ijdk.lang.*;
import org.incava.ijdk.text.LocationRange;

/**
 * Writes differences with context. Actually returns the differences as strings.
 * Writing is left to the invoker.
 */
public class DiffContextWriter extends DiffWriter {
    public DiffContextWriter(List<String> fromContents, List<String> toContents) {
        super(fromContents, toContents);
    }

    protected void printFrom(StringBuilder sb, FileDiff ref) {
        printLines(sb, true, ref, ref.getFirstLocation(), fromContents);
    }

    protected void printTo(StringBuilder sb, FileDiff ref) {
        printLines(sb, false, ref, ref.getSecondLocation(), toContents);
    }

    protected String getLine(List<String> lines, int lidx, int fromLine, int fromColumn, int toLine, int toColumn, boolean isDelete) {
        StringBuilder sb = new StringBuilder();
        sb.append("! ").append(lines.get(lidx - 1)).append(EOLN);
        return sb.toString();
    }

    protected void printLines(StringBuilder sb, boolean isDelete, FileDiff ref, LocationRange loc, List<String> lines) {
        int fromLine = loc.getStart().getLine();
        int fromColumn = loc.getStart().getColumn();
        int toLine = loc.getEnd().getLine();
        int toColumn = loc.getEnd().getColumn();
        
        for (int lnum = Math.max(0, fromLine - 4); lnum < fromLine - 1; ++lnum) {
            sb.append("  ").append(lines.get(lnum));
            sb.append(EOLN);
        }

        // PMD reports columns using tabSize == 8, so we replace tabs with
        // spaces here.
        // ... I loathe tabs.

        for (int lidx = fromLine; lidx <= toLine; ++lidx) {
            String line = getLine(lines, lidx, fromLine, fromColumn, toLine, toColumn, isDelete);
            sb.append(line);
        }

        for (int lnum = toLine; lnum < Math.min(toLine + 3, lines.size()); ++lnum) {
            sb.append("  ").append(lines.get(lnum));
            sb.append(EOLN);
        }
    }

    protected void printLines(StringBuilder sb, FileDiff ref) {
        ref.print(this, sb);
    }
}
