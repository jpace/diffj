package org.incava.diffj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.incava.analysis.BriefReport;
import org.incava.analysis.DetailedReport;
import org.incava.analysis.Report;
import org.incava.ijdk.util.ListExt;
import org.incava.qualog.Qualog;

public class DiffJ {
    private final Report report;
    private int exitValue;
    private final boolean recurseDirectories;
    private final String fromName;
    private final String toName;
    private final String fromSource;
    private final String toSource;
    
    public DiffJ(boolean briefOutput, boolean contextOutput, boolean highlightOutput, 
                 boolean recurseDirectories,
                 String fromName, String fromSource,
                 String toName, String toSource) {
        tr.Ace.set(true, 25, 4, 20, 25);
        tr.Ace.setOutput(Qualog.VERBOSE, Qualog.LEVEL4);
        tr.Ace.setOutput(Qualog.QUIET,   Qualog.LEVEL2);
        tr.Ace.setVerbose(true);
        tr.Ace.stack("this", this, 15);

        System.err.println("*******************************************************");

        this.report = briefOutput ? new BriefReport(System.out) : new DetailedReport(System.out, contextOutput, highlightOutput);
        this.recurseDirectories = recurseDirectories;
        this.fromName = fromName;
        this.toName = toName;
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

    protected File getFile(String name) {
        File file = null;
        if (name.equals("-")) {
            file = null;
        }
        else {
            file = new File(name);
        }
        return file;
    }

    public boolean isStdin(File file) {
        return file == null || file.getName().equals("-");
    }

    public void processNames(List<String> names) {
        tr.Ace.yellow("names", names);
        if (names.size() >= 2) {
            String toName = ListExt.get(names, -1);
            tr.Ace.yellow("toName", toName);
            File toFile = getFile(toName);
            tr.Ace.yellow("toFile", toFile);
            for (int ni = 0; ni < names.size() - 1; ++ni) {
                File fromFile = getFile(names.get(ni));
                tr.Ace.yellow("fromFile", fromFile);
                process(fromFile, toFile);
            }
        }
        else {
            System.err.println("usage: diffj from-file to-file");
            exitValue = 1;
        }
    }

    /**
     * Process the "files", which, if null, are considered to be standard input.
     */
    protected void processFiles(File from, File to) {
        tr.Ace.log("from: " + from + "; to: " + to);

        try {
            JavaFile fromFile = new JavaFile(from, fromName, fromSource);
            JavaFile toFile = new JavaFile(to, toName, toSource);
            final boolean flushReport = true;
            new JavaFileDiff(report, fromFile, toFile, flushReport);
        }
        catch (FileNotFoundException e) {
            System.out.println("Error opening file: " + e.getMessage());
            exitValue = -1;
        }
        catch (IOException e) {
            System.out.println("I/O error: " + e);
            exitValue = -1;
        }
        catch (Throwable t) {
            tr.Ace.log("t", t);
            t.printStackTrace();
            exitValue = -1;
        }
    }

    protected void processDirectories(JavaDirectory from, JavaDirectory to) {
        tr.Ace.setVerbose(true);

        List<String> fromFiles = from.getSubDirsAndJavaFiles();
        tr.Ace.onRed("fromFiles", fromFiles);
        List<JavaFSElement> fromElements = from.subelements();
        tr.Ace.onGreen("fromElements", fromElements);
        List<String> toFiles   = to.getSubDirsAndJavaFiles();
        tr.Ace.onRed("toFiles", toFiles);
        List<JavaFSElement> toElements = to.subelements();
        tr.Ace.onGreen("toElements", toElements);
        Set<String>  merged    = new TreeSet<String>();

        tr.Ace.setVerbose(false);
        
        merged.addAll(fromFiles);
        merged.addAll(toFiles);

        for (String fname : merged) {
            File fromFile = new File(from, fname);
            File toFile   = new File(to, fname);

            if (!verifyExists(fromFile) || !verifyExists(toFile)) {
                continue;
            }

            if (fromFile.isDirectory()) {
                if (toFile.isDirectory()) {
                    if (recurseDirectories) {
                        processDirectories(new JavaDirectory(fromFile, fromSource), new JavaDirectory(toFile, toSource));
                    }
                }
                else {
                    processDirFile(fromFile, toFile);
                }
            }
            else if (toFile.isDirectory()) {
                processFileDir(fromFile, toFile);
            }
            else {
                processFiles(fromFile, toFile);
            }
        }
    }

    protected static boolean isProcessedAsFile(File file) {
        return file == null || file.isFile() || file.getName().equals("-");
    }

    protected static boolean verifyExists(File file) {
        if (file.exists()) {
            return true;
        }
        else {
            System.err.println(file.getPath() + " does not exist");
            return false;
        }
    }

    protected void processDirFile(File fromDir, File toFile) {
        File fromFile = new File(fromDir, toFile.getPath());
        if (verifyExists(fromFile)) {
            processFiles(fromFile, toFile);
        }
    }

    protected void processFileDir(File fromFile, File toDir) {
        File toFile = new File(toDir, fromFile.getPath());
        if (verifyExists(toFile)) {
            processFiles(fromFile, toFile);
        }
    }
    
    protected void process(File from, File to) {
        tr.Ace.log("from: " + from + "; to: " + to);
        
        // stdin doesn't "exist", so we check for stdin first
        if (isProcessedAsFile(from) && isProcessedAsFile(to)) {
            processFiles(from, to);
        }
        else if (verifyExists(from) && verifyExists(to)) {
            if (from.isDirectory()) {
                if (to.isDirectory()) {
                    processDirectories(new JavaDirectory(from, fromSource), new JavaDirectory(to, toSource));
                }
                else {
                    processDirFile(from, to);
                }
            }
            else if (to.isDirectory()) {
                processFileDir(from, to);
            }
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
