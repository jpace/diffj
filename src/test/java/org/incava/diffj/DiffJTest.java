package org.incava.diffj;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.incava.analysis.BriefReport;
import org.incava.analysis.DetailedReport;
import org.incava.analysis.FileDiff;
import org.incava.analysis.Report;
import org.incava.diffj.io.JavaFile;
import org.incava.diffj.util.Lines;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.text.Location;
import org.incava.ijdk.text.LocationRange;
import org.incava.java.Java;
import org.incava.test.IncavaTestCase;

public class DiffJTest extends IncavaTestCase {
    public static final FileDiff[] NO_CHANGES = new FileDiff[0];
    
    public DiffJTest(String name) {
        super(name);
    }

    public String getSource() {
        return Java.SOURCE_1_5;
    }

    public boolean showContext() {
        return false;
    }

    public boolean highlight() {
        return true;
    }

    public Location loc(int line, int column) {
        return new Location(line, column);
    }

    public Location loc(int line, int col, String var) {
        return loc(line, col + var.length() - 1);
    }

    public Location loc(Location loc, String var) {
        return loc(loc.getLine(), loc.getColumn() + (var == null ? 0 : var.length() - 1));
    }

    public LocationRange locrg(int fromLine, int fromColumn, int toLine, int toColumn) {
        return new LocationRange(loc(fromLine, fromColumn), loc(toLine, toColumn));
    }

    public LocationRange locrg(int line, int fromColumn, int toColumn) {
        return new LocationRange(loc(line, fromColumn), loc(line, toColumn));
    }

    public LocationRange locrg(Location loc, String var) {
        return new LocationRange(loc, loc(loc, var));
    }

    public String[] getOutput(String fromLines, String toLines) {
        StringWriter writer = new StringWriter();

        evaluate(fromLines,
                 toLines,
                 Java.SOURCE_1_5, // source
                 makeDetailedReport(writer),
                 (FileDiff[])null);         // no code expectation comparisons
        
        // tr.Ace.setVerbose(true);
        // tr.Ace.red("*******************************************************");

        String[] lines = StringExt.split(writer.getBuffer().toString(), "\n");
        // tr.Ace.log("lines", lines);
        return lines;
    }

    public String[] getOutput(Lines fromLines, Lines toLines) {
        return getOutput(fromLines.toString(), toLines.toString());
    }

    public void evaluate(Lines fromLines, Lines toLines, FileDiff ... expectations) {
        evaluate(fromLines.toString(), toLines.toString(), expectations);
    }

    public void evaluate(Lines fromLines, Lines toLines, String src, FileDiff ... expectations) {
        evaluate(fromLines.toString(), toLines.toString(), src, expectations);
    }

    public void evaluate(Lines fromLines, Lines toLines, String src, Report report, FileDiff ... expectations) {
        evaluate(fromLines.toString(), toLines.toString(), src, report, expectations);
    }

    public void evaluate(String from, String to, FileDiff ... expectations) {
        evaluate(from, to, Java.SOURCE_1_5, expectations);
    }

    public void evaluate(String from, String to, String src, FileDiff ... expectations) {
        evaluate(from, to, src, makeReport(new StringWriter()), expectations);
    }

    public void evaluate(String fromStr, String toStr, String src, Report report, FileDiff ... expectations) {
        evaluate("-", fromStr, "-", toStr, src, report, expectations);
    }

    public void evaluate(String fromName, String fromStr, String toName, String toStr, String src, Report report, FileDiff ... expectations) {
        assertDiffsEqual(fromName, fromStr, toName, toStr, src, report, expectations);
    }

    public void assertDiffsEqual(String fromName, String fromStr, String toName, String toStr, String src, Report report, FileDiff ... expectations) {
        JavaFile fromFile = null;
        JavaFile toFile = null;
        try {
            fromFile = new JavaFile(fromName, fromStr, src);
            toFile = new JavaFile(toName, toStr, src);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertDiffsEqual(fromFile, toFile, report, expectations);
    }

    public void assertDiffsEqual(JavaFile fromFile, JavaFile toFile, Report report, FileDiff ... expectations) {
        Collection<FileDiff> diffs = report.getDifferences();

        try {
            fromFile.compare(report, toFile);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (expectations != null) {
            assertDifferencesEqual(expectations, diffs);
        }
        
        report.flush();
    }

    public void assertOutputEquals(Lines fromLines, Lines toLines, List<String> expected) {
        String[] output = getOutput(fromLines, toLines);
        tr.Ace.log("output", output);
        assertEquals(expected, Arrays.asList(output));
    }

    public void assertDifferencesEqual(FileDiff[] expectedDiffs, Collection<FileDiff> actualDiffs) {
        if (expectedDiffs.length != actualDiffs.size()) {
            tr.Ace.setVerbose(true);
                    
            tr.Ace.yellow("actualDiffs.size", actualDiffs.size());
            tr.Ace.yellow("actualDiffs", actualDiffs);
                    
            tr.Ace.yellow("expectedDiffs.length", expectedDiffs.length);
            tr.Ace.yellow("expectedDiffs", expectedDiffs);
                    
            assertEquals("number of differences", expectedDiffs.length, actualDiffs.size());
        }

        int di = 0;
        for (FileDiff actualDiff : actualDiffs) {
            assertDifferenceEqual(expectedDiffs[di], actualDiff, di);
            ++di;
        }
    }

    public void assertDifferenceEqual(FileDiff expectedDiff, FileDiff actualDiff, int di) {
        // tr.Ace.yellow("expectedDiff", expectedDiff);
        // tr.Ace.yellow("actualDiff", actualDiff);

        assertEquals("diff[" + di + "]", expectedDiff, actualDiff);
    }

    public Report makeReport(StringWriter writer) {
        return new BriefReport(writer);
    }

    public Report makeDetailedReport(StringWriter writer) {
        return new DetailedReport(writer, showContext(), highlight());
    }
}
