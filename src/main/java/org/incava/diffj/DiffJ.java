package org.incava.diffj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.incava.analysis.BriefReport;
import org.incava.analysis.DetailedReport;
import org.incava.analysis.Report;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.MultiMap;
import org.incava.qualog.Qualog;

public class DiffJ {
    private final Report report;
    private int exitValue;
    private final boolean recurseDirectories;
    private final String fromLabel;
    private final String toLabel;
    private final String fromSource;
    private final String toSource;
    
    public DiffJ(boolean briefOutput, boolean contextOutput, boolean highlightOutput, 
                 boolean recurseDirectories,
                 String fromLabel, String fromSource,
                 String toLabel, String toSource) {
        tr.Ace.set(true, 25, 4, 20, 25);
        tr.Ace.setOutput(Qualog.VERBOSE, Qualog.LEVEL4);
        tr.Ace.setOutput(Qualog.QUIET,   Qualog.LEVEL2);
        tr.Ace.setVerbose(true);
        tr.Ace.stack("this", this, 15);

        System.err.println("*******************************************************");

        this.report = briefOutput ? new BriefReport(System.out) : new DetailedReport(System.out, contextOutput, highlightOutput);
        this.recurseDirectories = recurseDirectories;
        this.fromLabel = fromLabel;
        this.toLabel = toLabel;
        this.fromSource = fromSource;
        this.toSource = toSource;
        this.exitValue = 0;
    }

    protected int getExitValue() {
        return exitValue;
    }

    protected void setExitValue(int ev) {
        exitValue = ev;
    }

    protected JavaFSElement getJavaElement(File file, String label, String source) {
        if (file == null || file.getName().equals("-") || (file.isFile() && verifyExists(file, label))) {
            return getJavaFile(file, label, source);
        }
        else if (file.isDirectory()) {
            return new JavaDirectory(file, source);
        }
        else {
            verifyExists(file, label);
        }
        return null;
    }

    public static String getName(File file, String label) {
        return label == null ? file.getAbsolutePath() : label;
    }

    protected JavaFile getJavaFile(File file, String label, String source) {
        try {
            return new JavaFile(file, label, source);
        }
        catch (FileNotFoundException e) {
            System.out.println("Error opening file '" + file.getAbsolutePath() + "': " + e.getMessage());
            exitValue = -1;
        }
        catch (IOException e) {
            System.out.println("I/O error with file '" + file.getAbsolutePath() + "': " + e);
            exitValue = -1;
        }
        return null;
    }

    public boolean isStdin(File file) {
        return file == null || file.getName().equals("-");
    }

    public void processNames(List<String> names) {
        tr.Ace.yellow("names", names);
        if (names.size() >= 2) {
            String lastName = ListExt.get(names, -1);
            tr.Ace.yellow("lastName", lastName);
            JavaFSElement toElement = getJavaElement(new File(lastName), toLabel, toSource);
            tr.Ace.yellow("toElement", toElement);
            if (toElement == null) {
                tr.Ace.onGreen("exitValue", "" + exitValue);
                return;
            }

            for (int ni = 0; ni < names.size() - 1; ++ni) {
                JavaFSElement fromFile = getJavaElement(new File(names.get(ni)), fromLabel, fromSource);
                tr.Ace.yellow("fromFile", fromFile);
                if (fromFile != null) {
                    process(fromFile, toElement, true);
                }
            }
        }
        else {
            System.err.println("usage: diffj from-file to-file");
            exitValue = 1;
        }

        tr.Ace.setVerbose(true);
        tr.Ace.onGreen("exitValue", "" + exitValue);
    }

    protected void process(JavaFSElement from, JavaFSElement to, boolean canReadDir) {
        tr.Ace.log("from: " + from + "; to: " + to);

        if (from instanceof JavaFile && to instanceof JavaFile) {
            JavaFile fromFile = (JavaFile)from;
            JavaFile toFile = (JavaFile)to;
            exitValue = fromFile.process(report, toFile, exitValue);
        }
        else if (from instanceof JavaFile && to instanceof JavaDirectory) {
            JavaFile fromFile = (JavaFile)from;
            JavaDirectory toDir = (JavaDirectory)to;
            JavaFile toFile = (JavaFile)getJavaElement(new File(toDir, fromFile.getName()), fromLabel, fromSource);
            if (toFile != null) {
                exitValue = fromFile.process(report, toFile, exitValue);
            }
        }
        else if (from instanceof JavaDirectory && to instanceof JavaFile) {
            JavaDirectory fromDir = (JavaDirectory)from;
            JavaFile toFile = (JavaFile)to;
            JavaFile fromFile = (JavaFile)getJavaElement(new File(fromDir, toFile.getName()), toLabel, toSource);
            if (fromFile != null) {
                exitValue = fromFile.process(report, toFile, exitValue);
            }
        }
        else if (from instanceof JavaDirectory && to instanceof JavaDirectory && canReadDir) {
            JavaDirectory fromDir = (JavaDirectory)from;
            JavaDirectory toDir = (JavaDirectory)to;
            processDirectories(fromDir, toDir, recurseDirectories);
        }
    }

    protected void processDirectories(JavaDirectory from, JavaDirectory to, boolean canRecurse) {
        tr.Ace.setVerbose(true);

        Set<String> names = new TreeSet<String>();
        names.addAll(from.getElementNames());
        names.addAll(to.getElementNames());
        
        for (String name : names) {
            tr.Ace.bold("name", name);

            JavaFSElement fromElmt = from.getElement(name);
            tr.Ace.bold("fromElmt", fromElmt);

            JavaFSElement toElmt = to.getElement(name);
            tr.Ace.bold("toElmt", toElmt);

            if (fromElmt == null || toElmt == null) {
                continue;
            }

            tr.Ace.setVerbose(false);

            process(fromElmt, toElmt, recurseDirectories);
        }

        tr.Ace.setVerbose(false);
    }

    protected boolean verifyExists(File file, String label) {
        if (file != null && file.exists()) {
            return true;
        }
        else {
            System.err.println(getName(file, label) + " does not exist");
            exitValue = 1;
            return false;
        }
    }

    public static void main(String[] args) {
        Options      opts  = Options.get();
        List<String> names = opts.process(Arrays.asList(args));
        DiffJ        diffj = new DiffJ(opts.showBriefOutput(), opts.showContextOutput(), opts.highlightOutput(),
                                       opts.recurse(),
                                       opts.getFirstFileName(), opts.getFromSource(),
                                       opts.getSecondFileName(), opts.getToSource());
        diffj.processNames(names);
        System.exit(diffj.exitValue);
    }
}
