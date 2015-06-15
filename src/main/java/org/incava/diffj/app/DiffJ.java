package org.incava.diffj.app;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import org.incava.analysis.BriefReport;
import org.incava.analysis.DetailedReport;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;
import org.incava.diffj.io.JavaElementFactory;
import org.incava.diffj.io.JavaFSElement;
import org.incava.diffj.lang.DiffJException;
import org.incava.ijdk.util.ListExt;

public class DiffJ {
    private final Report report;
    private int exitValue;
    private final boolean recurseDirectories;
    private final String fromLabel;
    private final String toLabel;
    private final String fromSource;
    private final String toSource;
    private final JavaElementFactory jef;
    private final FileDiffs fileDiffs;
    
    public DiffJ(boolean briefOutput, boolean contextOutput, boolean highlightOutput, 
                 boolean recurseDirectories,
                 String fromLabel, String fromSource,
                 String toLabel, String toSource) {
        // tr.Ace.set(true, 25, 4, 20, 25);
        // tr.Ace.setOutput(Log.VERBOSE, Log.LEVEL4);
        // tr.Ace.setOutput(Log.QUIET,   Log.LEVEL2);
        // tr.Ace.setVerbose(true);
        // tr.Ace.stack("this", this, 15);

        Writer writer = new OutputStreamWriter(System.out);
        this.report = briefOutput ? new BriefReport(writer) : new DetailedReport(writer, contextOutput, highlightOutput);
        this.recurseDirectories = recurseDirectories;
        this.fromLabel = fromLabel;
        this.toLabel = toLabel;
        this.fromSource = fromSource;
        this.toSource = toSource;
        this.exitValue = 0;
        this.jef = new JavaElementFactory();
        this.fileDiffs = report.getDifferences();
    }

    protected Report getReport() {
        return report;
    }

    protected int getExitValue() {
        return exitValue;
    }

    protected void setExitValue(int ev) {
        exitValue = ev;
    }

    protected JavaFSElement getJavaElement(File file, String label, String source) {
        try {
            return jef.createElement(file, label, source, recurseDirectories);
        }
        catch (DiffJException de) {
            // de.printStackTrace(System.out);
            System.err.println(de.getMessage());
            exitValue = 1;
            return null;
        }
    }

    public JavaFSElement getToElement(String toName) {
        return getJavaElement(new File(toName), toLabel, toSource);
    }

    public JavaFSElement getFromElement(String fromName) {
        return getJavaElement(new File(fromName), fromLabel, fromSource);
    }

    public boolean compareElements(String fromName, JavaFSElement toElmt) {
        try {
            JavaFSElement fromElmt = getFromElement(fromName);
            if (fromElmt == null) {
                return false;
            }
            fromElmt.compareTo(report, toElmt);
            if (fileDiffs.wasAdded()) {
                exitValue = 1;
            }
            return true;
        }
        catch (DiffJException de) {
            System.err.println(de.getMessage());
            exitValue = 1;
            return false;
        }
    }

    public void processNames(List<String> names) {
        if (names.size() < 2) {
            System.err.println("usage: diffj from-file to-file");
            exitValue = 1;
            return;
        }

        String lastName = ListExt.get(names, -1);
        JavaFSElement toElmt = getToElement(lastName);
        if (toElmt == null) {
            return;
        }

        for (int ni = 0; ni < names.size() - 1; ++ni) {
            if (!compareElements(names.get(ni), toElmt)) {
                break;
            }
        }
        tr.Ace.log("exitValue", "" + exitValue);
    }

    public static void main(String[] args) {
        Options opts = new Options();
        List<String> names = opts.process(Arrays.asList(args));

        if (opts.showVersion()) {
            System.out.println("diffj, version " + Options.VERSION);
            System.out.println("Written by Jeff Pace (jpace [at] incava [dot] org)");
            System.out.println("Released under the Lesser GNU Public License");
            System.exit(0);
        }

        DiffJ diffj = new DiffJ(opts.showBriefOutput(), opts.showContextOutput(), opts.highlightOutput(),
                                opts.recurse(),
                                opts.getFirstFileName(), opts.getFromSource(),
                                opts.getSecondFileName(), opts.getToSource());
        diffj.processNames(names);
        System.exit(diffj.exitValue);
    }
}
