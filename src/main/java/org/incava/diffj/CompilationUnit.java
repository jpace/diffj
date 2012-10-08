package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;

public class CompilationUnit {
    private final ASTCompilationUnit compUnit;

    public CompilationUnit(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void diff(ASTCompilationUnit other, Report report) {
        if (other == null) {
            return;
        }

        Differences differences = new Differences(report);
        FileDiffs fileDiffs = differences.getFileDiffs();
        
        tr.Ace.log("fileDiffs", fileDiffs);
        
        Packages pd = new Packages(fileDiffs);
        pd.compare(compUnit, other);
            
        Imports imps = new Imports(fileDiffs);
        imps.compare(compUnit, other);

        Types td = new Types(fileDiffs);
        td.compare(compUnit, other);
    }
}
