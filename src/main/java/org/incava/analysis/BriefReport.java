package org.incava.analysis;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;

/**
 * Reports differences briefly, vaguely a la "diff --brief".
 */
public class BriefReport extends Report {
    public BriefReport(Writer writer) {
        super(writer);
    }

    public BriefReport(OutputStream os) {
        super(os);
    }

    public BriefReport(Writer writer, String fromSource, String toSource) {
        super(writer, fromSource, toSource);
    }

    public BriefReport(Writer writer, File fromFile, File toFile) {
        super(writer, fromFile, toFile);
    }

    public BriefReport(OutputStream os, String fromSource, String toSource) {
        super(os, fromSource, toSource);
    }

    public BriefReport(OutputStream os, File fromFile, File toFile) {
        super(os, fromFile, toFile);
    }

    /**
     * Returns the given difference, in brief format.
     */
    protected String toString(FileDiff fdiff) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(toString(fdiff.getFirstLocation().getStart().getLine(),  fdiff.getFirstLocation().getEnd().getLine()));
        sb.append(fdiff.getType());
        sb.append(toString(fdiff.getSecondLocation().getStart().getLine(), fdiff.getSecondLocation().getEnd().getLine()));
        sb.append(": ");
        sb.append(fdiff.getMessage());
        sb.append(EOLN);
        
        return sb.toString();
    }

    /**
     * Writes all differences, and clears the list.
     */
    public void flush() {
        if (hasDifferences()) {
            printFileNames();
            writeDifferences();
        }
        clear();
    }

    /**
     * Writes the differences.
     */
    private void writeDifferences() {
        try {
            Collection<FileDiff> diffs = getDifferences();
            String lastStr = null;
            for (FileDiff fdiff : diffs) {
                String str = toString(fdiff);
                if (!str.equals(lastStr)) {
                    writer.write(str);
                    lastStr = str;
                }
            }
            // we can't close STDOUT
            writer.flush();
        }
        catch (IOException ioe) {
        }
    }
}
