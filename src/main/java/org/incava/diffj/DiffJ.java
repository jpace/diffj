package org.incava.diffj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.ijdk.io.*;
import org.incava.ijdk.util.TimedEvent;
import org.incava.ijdk.util.TimedEventSet;
import org.incava.java.*;
import org.incava.qualog.Qualog;

public class DiffJ {
    private TimedEventSet totalInit = new TimedEventSet();
    private TimedEventSet totalParse = new TimedEventSet();
    private TimedEventSet totalAnalysis = new TimedEventSet();

    private final Report report;
    private int exitValue;

    public DiffJ(String[] names, boolean briefOutput, boolean contextOutput, boolean highlightOutput) {
        tr.Ace.set(true, 25, 4, 20, 25);
        tr.Ace.setOutput(Qualog.VERBOSE, Qualog.LEVEL4);
        tr.Ace.setOutput(Qualog.QUIET,   Qualog.LEVEL2);

        if (briefOutput) {
            report = new BriefReport(System.out);
        }
        else {
            report = new DetailedReport(System.out, contextOutput, highlightOutput);
        }

        exitValue = 0;

        if (names.length >= 2) {
            tr.Ace.log("names[0]: " + names[0] + "; names[" + (names.length - 1) + "]: " + names[names.length - 1]);
            File toFile = new File(names[names.length - 1]);

            for (int ni = 0; ni < names.length - 1; ++ni) {
                File fromFile = new File(names[ni]);
                process(fromFile, toFile);
            }
        }
        else {
            System.err.println("usage: diffj from-file to-file");
            exitValue = 1;
        }
    }

    protected List<String> getJavaFiles(File fd) {
        return Arrays.asList(fd.list(new FilenameFilter() {
                public boolean accept(File dir, String pathname) {
                    File f = new File(dir, pathname);
                    return f.isDirectory() || (f.isFile() && pathname.endsWith(".java"));
                }
            }));
    }

    protected void process(File from, File to) {
        tr.Ace.log("from: " + from + "; to: " + to);

        boolean fromIsStdin = from.getName().equals("-");
        boolean toIsStdin   = to.getName().equals("-");
        
        if ((fromIsStdin || from.isFile()) && (toIsStdin || to.isFile())) {
            tr.Ace.log("from: " + from + "; to: " + to);

            try {
                String fromStr = null;
                FileReader fromRdr = fromIsStdin ? new FileReader(FileDescriptor.in) : new FileReader(from);
                fromStr = ReaderExt.readAsString(fromRdr, EnumSet.of(ReadOptionType.ADD_EOLNS));

                FileReader toRdr = null;
                String toStr = null;
                if (toIsStdin) {
                    if (fromIsStdin) {
                        toStr = fromStr; // need to clone this string?
                    }
                    else {
                        toRdr = new FileReader(FileDescriptor.in);
                    }
                }
                else {
                    toRdr = new FileReader(to);
                }

                if (toRdr != null) {
                    toStr = ReaderExt.readAsString(toRdr, EnumSet.of(ReadOptionType.ADD_EOLNS));
                }

                Options opts     = Options.get();
                String  fromName = opts.firstFileName  == null ? (fromIsStdin ? "-" : from.getPath()) : opts.firstFileName;
                String  toName   = opts.secondFileName == null ? (toIsStdin   ? "-" : to.getPath())   : opts.secondFileName;

                final boolean flushReport = true;
                new JavaFileDiff(report, fromName, fromStr, Options.get().fromSource, toName, toStr, Options.get().toSource, flushReport);

                report.flush();
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
                tr.Ace.log("from", from);
                tr.Ace.log("to", to);
                t.printStackTrace();
                exitValue = -1;
                System.exit(exitValue);
            }
        }
        else if (!from.exists()) {
            System.err.println(from.getPath() + " does not exist");
        }
        else if (!to.exists()) {
            System.err.println(to.getPath() + " does not exist");
        }
        else if (from.isDirectory()) {
            if (to.isDirectory()) {
                List<String> fromFiles = getJavaFiles(from);
                List<String> toFiles   = getJavaFiles(to);
                
                Set<String>  merged    = new TreeSet<String>();
            
                merged.addAll(fromFiles);
                merged.addAll(toFiles);

                for (String fname : merged) {
                    File fromFile = new File(from, fname);
                    File toFile   = new File(to, fname);

                    if (fromFile.isDirectory() && toFile.isDirectory()) {
                        if (Options.recurse.booleanValue()) {
                            process(fromFile, toFile);
                        }
                        else {
                            tr.Ace.log("not recursing");
                        }
                    }
                    else {
                        process(fromFile, toFile);
                    }
                }
            }
            else {
                File fromFile = new File(from, to.getPath());
                tr.Ace.log("fromFile: " + fromFile + " (" + fromFile.exists() + ")");
                process(fromFile, to);
            }
        }
        else if (to.isDirectory()) {
            File toFile = new File(to, from.getPath());
            tr.Ace.log("toFile: " + toFile + " (" + toFile.exists() + ")");
            process(from, toFile);
        }
    }

    public static void main(String[] args) {
        Options  opts  = Options.get();
        String[] names = opts.process(args);
        DiffJ    dj    = new DiffJ(names, opts.briefOutput, opts.contextOutput, opts.highlightOutput);
        System.exit(dj.exitValue);
    }
}
