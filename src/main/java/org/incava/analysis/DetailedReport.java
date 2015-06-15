package org.incava.analysis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import org.incava.ijdk.io.ReadOptionType;
import org.incava.ijdk.io.ReaderExt;
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
    protected List<String> fromContents;

    /**
     * The to-contents, separated by new lines, which are included at the end
     * of each string.
     */
    protected List<String> toContents;

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

    protected List<String> readReader(Reader rdr) {
        return ReaderExt.readLines(rdr, EnumSet.noneOf(ReadOptionType.class));
    }

    public void writeDifferences() {
        try {
            tr.Ace.stack("flushing differences");
            Collection<FileDiff> diffs = getDifferences();

            if (fromContents == null) {
                fromContents = readReader(fromFileRdr);
            }

            if (toContents == null) {
                toContents = readReader(toFileRdr);
            }

            DiffWriter dw = (showContext ? (highlight ? 
                                            new DiffContextHighlightWriter(fromContents, toContents) :
                                            new DiffContextWriter(fromContents, toContents)) :
                             new DiffNoContextWriter(fromContents, toContents));
                
            for (FileDiff fdiff : diffs) {
                String str = dw.getDifference(fdiff);
                writer.write(str);
            }
            writer.flush();
                
            // we can't close STDOUT:
            // writer.close();
        }
        catch (IOException ioe) {
            tr.Ace.log("ioe", ioe);
        }
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
