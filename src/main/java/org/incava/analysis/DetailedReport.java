package org.incava.analysis;

import java.awt.Point;
import java.io.*;
import java.util.*;
import org.incava.ijdk.io.*;
import org.incava.ijdk.lang.*;
import org.incava.qualog.Qualog;
import org.incava.ijdk.util.ANSI;


/**
 * Reports differences in long form.
 */
public class DetailedReport extends Report {
    /**
     * The number of spaces a tab is equivalent to.
     */
    public static int tabWidth = 4;

    /**
     * The reader associated with the from-file, which is used for reproducing
     * the code associated with a reference.
     */
    private Reader fromFileRdr;

    /**
     * The reader associated with the to-file, which is used for reproducing the
     * code associated with a reference.
     */
    private Reader toFileRdr;

    /**
     * The from-contents, separated by new lines, which are included at the end
     * of each string.
     */
    private String[] fromContents;

    /**
     * The to-contents, separated by new lines, which are included at the end
     * of each string.
     */
    private String[] toContents;

    /**
     * Whether to highlight.
     */
    private final boolean highlight;

    /**
     * Whether to show context.
     */
    private final boolean showContext;

    public DetailedReport(Writer writer, boolean showContext, boolean highlight) {
        super(writer);

        this.showContext = showContext;
        this.highlight = highlight;
    }

    public DetailedReport(OutputStream os, boolean showContext, boolean highlight) {
        super(os);

        this.showContext = showContext;
        this.highlight = highlight;
    }
    
    /**
     * Associates the given file with the list of references, including that are
     * adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(File fromFile, File toFile) {
        super.reset(fromFile, toFile);

        tr.Ace.log("fromFile: " + fromFile + "; toFile: " + toFile);
        
        fromContents = null;
        toContents = null;
        
        try {
            fromFileRdr = new FileReader(fromFile);
            toFileRdr   = new FileReader(toFile);
        }
        catch (IOException ioe) {
            tr.Ace.log("error reading files: " + fromFile + ", " + toFile);
        }
    }

    /**
     * Associates the given string source with the list of references, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(String fromSource, String toSource) {
        super.reset(fromSource, toSource);

        fromContents = null;
        toContents = null;
        
        fromFileRdr = new StringReader(fromSource);
        toFileRdr = new StringReader(toSource);
    }

    /**
     * Associates the given string source with the list of differences, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(String fromFileName, String fromContents, String toFileName, String toContents) {
        super.reset(fromFileName, fromContents, toFileName, toContents);

        this.fromContents = null;
        this.toContents = null;
        
        fromFileRdr = new StringReader(fromContents);
        toFileRdr = new StringReader(toContents);
    }

    /**
     * Writes all differences, and clears the list.
     */
    public void flush() {
        if (!differences.isEmpty()) {
            printFileNames();
            try {
                tr.Ace.log("flushing differences");
                Collection<FileDiff> diffs = collateDifferences(differences);
                for (FileDiff ref : diffs) {
                    String str = toString(ref);
                    writer.write(str);
                }
                // we can't close STDOUT
                writer.flush();
                // writer.close();
            }
            catch (IOException ioe) {
            }
        }
        clear();
    }

    /**
     * Returns a string representing the given reference, consistent with the
     * format of the Report subclass.
     */
    protected String toString(FileDiff ref) {
        StringBuilder sb = new StringBuilder();

        if (fromContents == null) {
            fromContents = ReaderExt.readlines(fromFileRdr);
        }

        if (toContents == null) {
            toContents = ReaderExt.readlines(toFileRdr);
        }

        DiffWriter dw = null;

        if (showContext) {
            if (highlight) {
                dw = new DiffContextHighlightWriter(fromContents, toContents);
            }
            else {
                dw = new DiffContextWriter(fromContents, toContents);
            }
        }
        else {
            dw = new DiffNoContextWriter(fromContents, toContents);
        }

        return dw.getDifference(ref);
    }
}
