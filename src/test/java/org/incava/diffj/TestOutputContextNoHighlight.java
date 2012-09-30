package org.incava.diffj;

import org.incava.analysis.DetailedReport;
import org.incava.analysis.Report;

public class TestOutputContextNoHighlight extends OutputContextTest {
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
