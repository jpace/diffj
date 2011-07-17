package org.incava.diffj;

import java.awt.Point;
import java.io.*;
import java.util.*;
import junit.framework.TestCase;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.ijdk.io.*;
import org.incava.ijdk.lang.*;
import org.incava.java.*;
import org.incava.test.AbstractTestCaseExt;


public class AbstractDiffJTest extends AbstractTestCaseExt {

    public static final FileDiff[] NO_CHANGES = new FileDiff[0];

    private Report _report;
    
    public AbstractDiffJTest(String name) {
        super(name);
    }

    public String getSource() {
        return Java.SOURCE_1_5;
    }

    public boolean showContext() {
        return false;
    }

    public Point loc(int line, int column) {
        return new Point(line, column);
    }

    public Point loc(int line, int col, String var) {
        return loc(line, col + var.length() - 1);
    }

    public Point loc(Point pt, String var) {
        return loc(pt.x, pt.y + (var == null ? 0 : var.length() - 1));
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
        evaluate(from, to, Java.SOURCE_1_3, expectations);
    }

    public void evaluate(String from, String to, String src, FileDiff ... expectations) {
        evaluate(from, to, src, makeReport(new StringWriter()), expectations);
    }

    public void evaluate(String from, String to, String src, Report report, FileDiff ... expectations) {
        _report = report;
        
        Collection<FileDiff> diffs = _report.getDifferences();

        _report.reset(from, to);

        try {
            StringReader fromRdr = new StringReader(from);
            StringReader toRdr = new StringReader(to);

            JavaCharStream jcsFrom = new JavaCharStream(fromRdr);
            JavaCharStream jcsTo = new JavaCharStream(toRdr);

            JavaParser fromParser = new JavaParser(jcsFrom);
            JavaParser toParser = new JavaParser(jcsTo);

            tr.Ace.setVerbose(true);

            if (src.equals(Java.SOURCE_1_3)) {
                fromParser.setJDK13();
                toParser.setJDK13();
            }
            else if (src.equals(Java.SOURCE_1_4)) {
                // nothing.
            }
            else if (src.equals(Java.SOURCE_1_5) || src.equals(Java.SOURCE_1_6)) {
                fromParser.setJDK15();
                toParser.setJDK15();
            }
            else {
                fail("ERROR: source version '" + src + "' not recognized");
            }

            ASTCompilationUnit fromCu = fromParser.CompilationUnit();
            ASTCompilationUnit toCu   = toParser.CompilationUnit();
            
            CompilationUnitDiff cud = new CompilationUnitDiff(_report, false);
            cud.compare(fromCu, toCu);
            
            if (expectations == null) {
                // skipping expectations check
            }
            else {
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

                    assertEquals("expectations[" + di + "].type",    exp.type,    ref.type);
                    assertEquals("expectations[" + di + "].message", exp.message, ref.message);

                    Point[][] pts = new Point[][] {
                        { exp.firstStart,  ref.firstStart  },
                        { exp.secondStart, ref.secondStart },
                        { exp.firstEnd,    ref.firstEnd    },
                        { exp.secondEnd,   ref.secondEnd   },
                    };
                    for (int pi = 0; pi < pts.length; ++pi) {
                        assertEquals("expectations[" + di + "][" + pi + "]", pts[pi][0], pts[pi][1]);
                    }

                    ++di;
                }
            }

            _report.flush();
        }
        catch (ParseException e) {
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
        return _report;
    }
}
