package org.incava.diffj;

import org.incava.analysis.DetailedReport;
import org.incava.analysis.Report;

public class OutputContextNoHighlightTest extends OutputContextTest {
    public OutputContextNoHighlightTest(String name) {
        super(name);
    }

    public boolean highlight() {
        return false;
    }

    public String adorn(String str, boolean isDelete) {
        return str;
    }
}
