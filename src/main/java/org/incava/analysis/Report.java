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
     * Whether any differences were stored in this report.
     */
    private boolean hadDifferences;

    /**
     * Creates a report for the given writer.
     *
     * @param writer The writer associated with this report.
     */
    public Report(Writer writer) {
        this.writer = writer;
        hadDifferences = false;
        differences = new FileDiffs();
    }
    
    /**
     * Associates the given file with the list of differences, including that are
     * adding to this report later, i.e., prior to <code>flush</code>.
     *
     * @param fromFile The from-file, containing source code, to which this report applies.
     * @param toFile The to-file, containing source code, to which this report applies.
     */
    public void resetFiles(File fromFile, File toFile) {
        this.fromFileName = fromFile.getPath();
        this.toFileName   = toFile.getPath();
    }

    /**
     * Associates the given string source with the list of differences, including
     * that are adding to this report later, i.e., prior to <code>flush</code>.
     */
    public void reset(String fromSource, String toSource) {
        this.fromFileName = "-";
        this.toFileName   = "-";
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
     * Writes all differences, and clears the list.
     */
    public abstract void flush();
    
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
            }
            
            fromFileName = null;
            toFileName = null;
        }
    }
    
    /**
     * Returns a string representing the given reference, consistent with the
     * format of the Report subclass.
     */
    protected abstract String toString(FileDiff fdiff);

    /**
     * Sends the given string to the writer associated with this Report.
     *
     * @param str The string to be written.
     */
    protected void write(String str) {
        tr.Ace.log("writing '" + str + "'");
        try {
            writer.write(str);
        }
        catch (IOException ioe) {
        }
    }

    protected String toString(int x, int y) {
        StringBuilder sb = new StringBuilder();
        sb.append(x);
        if (x != y) {
            sb.append(",").append(y);
        }
        return sb.toString();
    }

    /**
     * Clears the list of differences.
     */
    protected void clear() {
        tr.Ace.yellow("differences", differences);
        differences.clear();
        tr.Ace.yellow("differences", differences);
    }

    /**
     * Returns whether there are or were (meaning already flushed) differences.
     */
    public boolean hadDifferences() {
        return hadDifferences;
    }

    public boolean hasDifferences() {
        return !differences.isEmpty();
    }
}
