package org.incava.diffj;

import java.io.File;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.incava.analysis.BriefReport;
import org.incava.analysis.DetailedReport;
import org.incava.analysis.Report;
import org.incava.ijdk.io.ReadOptionType;
import org.incava.ijdk.io.ReaderExt;
import org.incava.ijdk.util.ListExt;
import org.incava.ijdk.util.TimedEvent;
import org.incava.ijdk.util.TimedEventSet;
import org.incava.qualog.Qualog;

public class DiffJ {
    private TimedEventSet totalInit = new TimedEventSet();
    private TimedEventSet totalParse = new TimedEventSet();
    private TimedEventSet totalAnalysis = new TimedEventSet();

    private final Report report;
    private int exitValue;

    public DiffJ(List<String> names, boolean briefOutput, boolean contextOutput, boolean highlightOutput) {
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

        if (names.size() >= 2) {
            File toFile = new File(ListExt.get(names, -1));
            
            for (int ni = 0; ni < names.size() - 1; ++ni) {
                File fromFile = new File(names.get(ni));
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
        Options      opts  = Options.get();
        List<String> names = opts.process(Arrays.asList(args));
        DiffJ        diffj = new DiffJ(names, opts.briefOutput, opts.contextOutput, opts.highlightOutput);
        System.exit(diffj.exitValue);
    }
}
