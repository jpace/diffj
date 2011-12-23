package org.incava.analysis;

import java.awt.Point;
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
    protected String toString(FileDiff ref) {
        StringBuffer buf = new StringBuffer();

        Point del = new Point(ref.firstStart.x,  ref.firstEnd.x);
        Point add = new Point(ref.secondStart.x, ref.secondEnd.x);
        FileDiff.Type ind = ref.type;
        
        buf.append(toString(del));
        buf.append(ref.type);
        buf.append(toString(add));
        buf.append(": ");
        buf.append(ref.message);
        buf.append(EOLN);
        
        return buf.toString();
    }

    /**
     * Writes all differences, and clears the list.
     */
    public void flush() {
        if (!differences.isEmpty()) {
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
            Collection<FileDiff> diffs = collateDifferences(differences);
            String lastStr = null;
            for (FileDiff ref : diffs) {
                String str = toString(ref);
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
