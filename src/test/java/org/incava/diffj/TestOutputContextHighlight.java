package org.incava.diffj;

import java.io.StringWriter;
import org.incava.analysis.DetailedReport;
import org.incava.analysis.Report;
import org.incava.ijdk.util.ANSI;

public class TestOutputContextHighlight extends AbstractTestOutputContext {
    public TestOutputContextHighlight(String name) {
        super(name);
    }

    public String adorn(String str, boolean isDelete) {
        StringBuilder sb = new StringBuilder();
        sb.append(isDelete ? ANSI.RED : ANSI.YELLOW);
        sb.append(str);
        sb.append(ANSI.RESET);

        return sb.toString();
    }

    public Report makeDetailedReport(StringWriter output) {
        return new DetailedReport(output, true, true);
    }

    public Report makeDetailedReport(StringWriter output, boolean showContext) {
        return new DetailedReport(output, showContext, true);
    }

    public Report makeReport(StringWriter output) {
        return new DetailedReport(output, true, true);
    }
}
