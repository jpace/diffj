package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;

public class CompilationUnits {
    private final Differences differences;

    public CompilationUnits(Report report) {
        this.differences = new Differences(report);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        FileDiffs diffs = differences.getFileDiffs();
        tr.Ace.log("diffs", diffs);
        
        if (a != null && b != null) {
            Packages pd = new Packages(diffs);
            pd.compare(a, b);
            
            Imports imps = new Imports(diffs);
            imps.compare(a, b);

            Types td = new Types(diffs);
            td.compare(a, b);
        }
    }
}
