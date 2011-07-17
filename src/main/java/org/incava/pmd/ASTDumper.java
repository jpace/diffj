
package org.incava.pmd;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;


public class ASTDumper {
    public static void main(String[] args) {
        long totalInitTime  = 0;
        long totalParseTime = 0;
        long totalDumpTime  = 0;

        JavaParser parser = null;
        int ai = 0;
        boolean astDump = false;
        
        if (ai < args.length && args[ai].equals("--dump")) {
            astDump = true;
            ++ai;
        }

        for (; ai < args.length; ++ai) {
            String filename = args[ai];
            long   initTime;

            // System.out.println("Reading from file " + filename + " . . .");
            try {
                long initStartTime = System.currentTimeMillis();
                JavaCharStream jcs = new JavaCharStream(new FileInputStream(filename));
                if (parser == null) {
                    parser = new JavaParser(jcs);
                }
                else {
                    parser.ReInit(jcs);
                }
                long initStopTime = System.currentTimeMillis();
                initTime = initStopTime - initStartTime;
                totalInitTime += initTime;
            }
            catch (FileNotFoundException e) {
                System.out.println("File not found: " + filename);
                continue;
            }

            try {
                long parseStartTime = System.currentTimeMillis();
                ASTCompilationUnit cu = parser.CompilationUnit();
                
                long parseStopTime  = System.currentTimeMillis();
                long parseTime      = parseStopTime - parseStartTime;

                totalParseTime += parseTime;

                long dumpTime = 0;

                if (astDump) {
                    long dumpStartTime = System.currentTimeMillis();

                    cu.dump("");

                    long dumpStopTime = System.currentTimeMillis();
                    dumpTime = dumpStopTime - dumpStartTime;
                    totalDumpTime += dumpTime;
                }

                System.out.println("    " + filename);
                System.out.println("        init time : " + initTime  + " ms.");
                System.out.println("        parse time: " + parseTime + " ms.");
                if (astDump) {
                    System.out.println("        dump time : " + dumpTime  + " ms.");
                }
                System.out.println("        total time: " + (initTime + parseTime) + " ms.");
            }
            catch (ParseException e) {
                System.out.println(e.getMessage());
                System.out.println("Encountered errors during parse.");
            }
        }

        System.out.println("    " + args.length + " files");
        System.out.println("        init time : " + totalInitTime  + " ms.");
        System.out.println("        parse time: " + totalParseTime + " ms.");
        if (astDump) {
            System.out.println("        dump time : " + totalDumpTime  + " ms.");
        }
        System.out.println("        total time: " + (totalInitTime + totalParseTime + totalDumpTime) + " ms.");
    }

}
