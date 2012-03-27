package org.incava.diffj;

import java.util.Collection;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;

public class CompilationUnitDiff extends DiffComparator {
    public CompilationUnitDiff(Report report) {
        super(report);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        FileDiffs diffs = getFileDiffs();
        tr.Ace.log("diffs", diffs);
        
        if (a != null && b != null) {
            PackageDiff pd = new PackageDiff(diffs);
            pd.compare(a, b);
            
            ImportsDiff id = new ImportsDiff(diffs);
            id.compare(a, b);

            TypesDiff td = new TypesDiff(diffs);
            td.compare(a, b);
        }
    }
}
