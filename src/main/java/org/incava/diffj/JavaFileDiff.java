package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.Report;

/**
 * Compares two Java files.
 */
public class JavaFileDiff {
    private int exitValue;

    public JavaFileDiff(Report report, JavaFile fromFile, JavaFile toFile, boolean flushReport) {
        exitValue = 0;

        ASTCompilationUnit fromCu = compile(fromFile);
        ASTCompilationUnit toCu   = compile(toFile);
        
        report.reset(fromFile.getLabel(), fromFile.getContents(), toFile.getLabel(), toFile.getContents());
        
        CompilationUnitDiff cud = new CompilationUnitDiff(report, flushReport);
        // chew the cud here ...
        cud.compare(fromCu, toCu);
        
        if (report.getDifferences().size() > 0) {
            exitValue = 1;
        }

        if (flushReport) {
            report.flush();
        }
    }

    protected ASTCompilationUnit compile(JavaFile file) {
        try {
            return file.compile();
        }
        catch (DiffJException de) {
            tr.Ace.red("de", de);
            System.err.println("Error: " + de.getMessage());
            exitValue = 1;
            return null;
        }
    }

    public int getExitValue() {
        return exitValue;
    }
}
