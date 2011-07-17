package org.incava.diffj;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import org.incava.analysis.*;
import org.incava.ijdk.lang.*;
import org.incava.ijdk.util.ANSI;


public class TestOutputContextNoHighlight extends AbstractTestOutputContext {
    
    public TestOutputContextNoHighlight(String name) {
        super(name);
    }

    public String adorn(String str, boolean isDelete) {
        return str;
    }

    public Report makeDetailedReport(StringWriter output) {
        return new DetailedReport(output, true, false);
    }

    public Report makeDetailedReport(StringWriter output, boolean showContext) {
        return new DetailedReport(output, showContext, false);
    }

    public Report makeReport(StringWriter output) {
        return new DetailedReport(output, true, false);
    }
}
