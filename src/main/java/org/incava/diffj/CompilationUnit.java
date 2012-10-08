package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;

public class CompilationUnit {
    private final DiffComparator differences;

    public CompilationUnit(Report report) {
        this.differences = new DiffComparator(report);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        FileDiffs diffs = differences.getFileDiffs();
        tr.Ace.log("diffs", diffs);
        
        if (a != null && b != null) {
            Package pd = new Package(diffs);
            pd.compare(a, b);
            
            Imports imps = new Imports(diffs);
            imps.compare(a, b);

            TypesDiff td = new TypesDiff(diffs);
            td.compare(a, b);
        }
    }
}
