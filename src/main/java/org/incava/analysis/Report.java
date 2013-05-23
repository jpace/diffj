package org.incava.analysis;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import org.incava.ijdk.io.FileExt;
import org.incava.ijdk.io.IO;

/**
 * Reports errors (differences), in a format that is determined by the subclass.
 */
public abstract class Report {
    /**
     * The end-of-line character/sequence for this OS.
     */
    protected final static String EOLN = IO.EOLN;

    /**
     * The from-file to which this report currently applies. By default, this is
     * '-', denoting standard output.
     */
    protected String fromFileName = "-";

    /**
     * The to-file to which this report currently applies. By default, this is
     * '-', denoting standard output.
     */
    protected String toFileName = "-";

    /**
     * The writer to which this report sends output.
     */
    protected Writer writer;

    /**
     * The set of differences, which are maintained in sorted order.
     */
    private FileDiffs differences;

    /**
     * Creates a report for the given writer.
     *
     * @param writer The writer associated with this report.
     */
    public Report(Writer writer) {
        this.writer = writer;
        differences = new FileDiffs();
    }

    /**
     * Associates the given string source with the list of differences, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(String fromFileName, String fromContents, String toFileName, String toContents) {
        this.fromFileName = fromFileName;
        this.toFileName   = toFileName;
    }

    /**
     * Writes all differences.
     */
    public abstract void writeDifferences();

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
    
    public FileDiffs getDifferences() {
        return differences;
    }

    public void printFileNames() {
        // only print file names once per report.
        // extend this for unified (file name per line)
        
        if (fromFileName != null && toFileName != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(fromFileName);
            sb.append(" <=> ");
            sb.append(toFileName);
            sb.append(System.getProperty("line.separator"));
            
            try {
                writer.write(sb.toString());
            }
            catch (IOException ioe) {
                tr.Ace.log("ioe", ioe);
            }
            
            fromFileName = null;
            toFileName = null;
        }
    }    

    /**
     * Clears the list of differences.
     */
    protected void clear() {
        differences.clear();
    }

    public boolean hasDifferences() {
        return !differences.isEmpty();
    }
}
