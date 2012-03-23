package org.incava.analysis;

import java.util.List;
import org.incava.ijdk.util.ANSI;

/**
 * Writes differences with context, and color!
 */
public class DiffContextHighlightWriter extends DiffContextWriter {
    /**
     * The color for added code.
     */
    protected static String COLOR_ADDED = ANSI.YELLOW;

    /**
     * The color for deleted code.
     */
    protected static String COLOR_DELETED = ANSI.RED;

    public DiffContextHighlightWriter(List<String> fromContents, List<String> toContents) {
        super(fromContents, toContents);
    }

    protected String getLine(List<String> lines, int lidx, int fromLine, int fromColumn, int toLine, int toColumn, boolean isDelete) {
        String line = lines.get(lidx - 1);

        StringBuilder sb = new StringBuilder();

        // PMD reports columns using tabSize == 8, so we replace tabs with
        // spaces here.
        // ... I loathe tabs.
        
        line = line.replace("\t", "        ");
        
        int llen = line.length();
        
        // columns are 1-indexed, strings are 0-indexed
        // ... half my life is adding or substracting one.
        
        int fcol = fromLine == lidx ? fromColumn - 1 : 0;
        int tcol = toLine   == lidx ? toColumn       : llen;
        
        sb.append("! ").append(line.substring(0, fcol));
        
        // highlight:

        String highlightColor = isDelete ? COLOR_DELETED : COLOR_ADDED;

        sb.append(highlightColor);
        sb.append(line.substring(fcol, tcol));
        sb.append(ANSI.RESET);
        
        sb.append(line.substring(tcol, llen));
        sb.append(EOLN);

        return sb.toString();
    }
}
