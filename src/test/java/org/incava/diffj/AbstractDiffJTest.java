package org.incava.diffj;

import java.io.StringWriter;
import java.util.Collection;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ParseException;
import org.incava.analysis.BriefReport;
import org.incava.analysis.DetailedReport;
import org.incava.analysis.FileDiff;
import org.incava.analysis.Report;
import org.incava.ijdk.lang.StringExt;
import org.incava.ijdk.text.Location;
import org.incava.java.Java;
import org.incava.test.AbstractTestCaseExt;

public class AbstractDiffJTest extends AbstractTestCaseExt {
    public static final FileDiff[] NO_CHANGES = new FileDiff[0];
    private Report report;
    
    public AbstractDiffJTest(String name) {
        super(name);
    }

    public String getSource() {
        return Java.SOURCE_1_5;
    }

    public boolean showContext() {
        return false;
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

    public String[] getOutput(String fromLines, String toLines) {
        StringWriter writer = new StringWriter();

        evaluate(fromLines,
                 toLines,
                 Java.SOURCE_1_5, // source
                 makeDetailedReport(writer, showContext()),
                 (FileDiff[])null);         // no code expectation comparisons
        
        tr.Ace.setVerbose(true);
        tr.Ace.red("*******************************************************");

        String[] lines = StringExt.split(writer.getBuffer().toString(), "\n");
        tr.Ace.log("lines", lines);

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
        this.report = report;

        Collection<FileDiff> diffs = this.report.getDifferences();

        try {
            JavaFile fromFile = new JavaFile(fromName, fromStr, src);
            tr.Ace.cyan("fromFile", fromFile);
            tr.Ace.cyan("fromStr", fromStr);
            JavaFile toFile = new JavaFile(toName, toStr, src);
            tr.Ace.green("toFile", toFile);
            tr.Ace.green("toStr", toStr);

            fromFile.compare(report, toFile);
            
            if (expectations != null) {
                if (expectations.length != diffs.size()) {
                    tr.Ace.setVerbose(true);
                    
                    tr.Ace.yellow("diffs.size", String.valueOf(diffs.size()));
                    tr.Ace.yellow("diffs", diffs);
                    
                    tr.Ace.yellow("expectations.length", String.valueOf(expectations.length));
                    tr.Ace.yellow("expectations", expectations);
                    
                    assertEquals("number of differences", expectations.length, diffs.size());
                }

                int di = 0;
                for (FileDiff ref : diffs) {
                    FileDiff exp = expectations[di];

                    tr.Ace.yellow("exp", exp);
                    tr.Ace.yellow("ref", ref);

                    assertNotNull("reference not null", ref);

                    assertEquals("expectations[" + di + "].type",    exp.getType(),    ref.getType());
                    assertEquals("expectations[" + di + "].message", exp.getMessage(), ref.getMessage());
                    assertEquals("expectations[" + di + "].first",   exp.getFirstLocation(), ref.getFirstLocation());
                    assertEquals("expectations[" + di + "].second",  exp.getSecondLocation(), ref.getSecondLocation());

                    ++di;
                }
            }

            this.report.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public Report makeReport(StringWriter output) {
        return new BriefReport(output);
    }

    public Report makeDetailedReport(StringWriter output) {
        return new DetailedReport(output, true, true);
    }

    public Report makeDetailedReport(StringWriter output, boolean showContext) {
        return new DetailedReport(output, showContext, true);
    }

    public Report getReport() {
        return report;
    }
}
