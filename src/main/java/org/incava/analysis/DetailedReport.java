package org.incava.analysis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import org.incava.ijdk.io.*;
import org.incava.ijdk.lang.*;
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
    private List<String> fromContents;

    /**
     * The to-contents, separated by new lines, which are included at the end
     * of each string.
     */
    private List<String> toContents;

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
        this(new OutputStreamWriter(os), showContext, highlight);
    }
    
    /**
     * Associates the given file with the list of references, including that are
     * adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(File fromFile, File toFile) {
        super.resetFiles(fromFile, toFile);

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

        this.fromContents = null;
        this.toContents = null;
        
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
        if (hasDifferences()) {
            printFileNames();
            try {
                tr.Ace.stack("flushing differences");
                Collection<FileDiff> diffs = getDifferences();
                for (FileDiff fdiff : diffs) {
                    String str = toString(fdiff);
                    writer.write(str);
                }
                writer.flush();
                
                // we can't close STDOUT:
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
    protected String toString(FileDiff fdiff) {
        StringBuilder sb = new StringBuilder();

        if (fromContents == null) {
            fromContents = ReaderExt.readLines(fromFileRdr, EnumSet.noneOf(ReadOptionType.class));
        }

        if (toContents == null) {
            toContents = ReaderExt.readLines(toFileRdr, EnumSet.noneOf(ReadOptionType.class));
        }

        DiffWriter dw = (showContext ? (highlight ? 
                                        new DiffContextHighlightWriter(fromContents, toContents) :
                                        new DiffContextWriter(fromContents, toContents)) :
                         new DiffNoContextWriter(fromContents, toContents));
        
        return dw.getDifference(fdiff);
    }

    // public void printFileNames() {
    //     // extend this for unified (file name per line)

    //     if (fromFileName != null && toFileName != null) {
    //         String lnsep = System.getProperty("line.separator");
    //         StringBuilder sb = new StringBuilder();
    //         sb.append("===================================================================").append(lnsep);
    //         sb.append("--- " + fromFileName).append(lnsep);
    //         sb.append("+++ " + fromFileName).append(lnsep);
            
    //         try {
    //             writer.write(sb.toString());
    //         }
    //         catch (IOException ioe) {
    //         }
            
    //         fromFileName = null;
    //         toFileName = null;
    //     }
    // }
}
