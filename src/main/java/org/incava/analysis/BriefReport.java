package org.incava.analysis;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * Reports differences briefly, vaguely a la "diff --brief".
 */
public class BriefReport extends Report {
    public BriefReport(Writer writer) {
        super(writer);
    }

    /**
     * Returns the given difference, in brief format.
     */
    protected String toString(FileDiff fdiff) {
        StringBuilder sb = new StringBuilder();
        sb.append(fdiff.toDiffSummaryString());
        sb.append(": ");
        sb.append(fdiff.getMessage());
        sb.append(EOLN);
        
        return sb.toString();
    }

    /**
     * Writes the differences.
     */
    public void writeDifferences() {
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
            tr.Ace.log("ioe", ioe);
        }
    }
}
