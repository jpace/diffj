package org.incava.diffj;

import java.io.StringWriter;
import org.incava.analysis.DetailedReport;
import org.incava.analysis.Report;

public class TestOutputContextNoHighlight extends AbstractTestOutputContext {
    public TestOutputContextNoHighlight(String name) {
        super(name);
    }

    public boolean highlight() {
        return false;
    }

    public String adorn(String str, boolean isDelete) {
        return str;
    }
}
