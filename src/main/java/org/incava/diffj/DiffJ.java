package org.incava.diffj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.ijdk.io.*;
import org.incava.java.*;
import org.incava.qualog.Qualog;
import org.incava.ijdk.util.Arrays;
import org.incava.ijdk.util.TimedEvent;
import org.incava.ijdk.util.TimedEventSet;


public class DiffJ {

    private TimedEventSet totalInit = new TimedEventSet();

    private TimedEventSet totalParse = new TimedEventSet();

    private TimedEventSet totalAnalysis = new TimedEventSet();

    private final Report report;

    private int exitValue;

    public DiffJ(String[] args) {
        tr.Ace.set(true, 25, 4, 20, 25);
        tr.Ace.setOutput(Qualog.VERBOSE, Qualog.LEVEL4);
        tr.Ace.setOutput(Qualog.QUIET,   Qualog.LEVEL2);

        Options  opts  = Options.get();
        String[] names = opts.process(args);

        if (opts.briefOutput) {
            report = new BriefReport(System.out);
        }
        else {
            report = new DetailedReport(System.out, opts.contextOutput, opts.highlightOutput);
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
        return java.util.Arrays.asList(fd.list(new FilenameFilter() {
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
                fromStr = FileExt.read(fromRdr, true);

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
                    toStr = FileExt.read(toRdr, true);
                }
                        
                ASTCompilationUnit fromCu = compile(from.getName(), new StringReader(fromStr), Options.get().fromSource);
                ASTCompilationUnit toCu   = compile(to.getName(),   new StringReader(toStr),   Options.get().toSource);

                report.reset(fromIsStdin ? "-" : from.getPath(), fromStr, toIsStdin ? "-" : to.getPath(), toStr);
            
                CompilationUnitDiff cud = new CompilationUnitDiff(report, true);
                // chew the cud here ...
                cud.compare(fromCu, toCu);
                if (report.getDifferences().size() > 0) {
                    exitValue = 1;
                }

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

    protected ASTCompilationUnit compile(String name, Reader rdr, String sourceVersion) {
        TimedEvent init = new TimedEvent(totalInit);
        try {
            JavaCharStream jcs = new JavaCharStream(rdr);
            JavaParser parser = new JavaParser(jcs);

            if (sourceVersion.equals(Java.SOURCE_1_3)) {
                tr.Ace.log("creating 1.3 parser");
                parser.setJDK13();
            }
            else if (sourceVersion.equals(Java.SOURCE_1_4)) {
                tr.Ace.log("creating 1.4 parser");
            }
            else if (sourceVersion.equals(Java.SOURCE_1_5) || sourceVersion.equals(Java.SOURCE_1_6)) {
                // no setJDK16 yet in PMD
                tr.Ace.log("creating 1.5 parser");
                parser.setJDK15();
            }
            else {
                System.err.println("ERROR: source version '" + sourceVersion + "' not recognized");
                System.exit(-1);
            }
            
            init.end();

            // tr.Ace.log("running parser");
            
            TimedEvent         parse = new TimedEvent(totalParse);
            ASTCompilationUnit cu    = parser.CompilationUnit();
            parse.end();

            long total = init.duration + parse.duration; // + analysis.duration;
            tr.Ace.log("time: total: " + total + "; init: " + init.duration + "; parse: " + parse.duration + "; " + name);
            // tr.Ace.log("time: total: " + total + "; init: " + init.duration + "; parse: " + parse.duration + "; analysis: " + analysis.duration + "; " + name);

            return cu;
        }
        catch (TokenMgrError tme) {
            System.out.println("Error parsing (tokenizing) " + name + ": " + tme.getMessage());
            exitValue = 1;
            return null;
        }
        catch (ParseException e) {
            System.out.println("Parse error in " + name + ": " + e.getMessage());
            exitValue = -1;
            return null;
        }
    }

    public static void main(String[] args) {
        DiffJ dj = new DiffJ(args);
        System.exit(dj.exitValue);
    }

}
