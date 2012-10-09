package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;
import org.incava.pmdx.CompilationUnitUtil;

public class CompilationUnit {
    private final ASTCompilationUnit compUnit;

    public CompilationUnit(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void diff(ASTCompilationUnit toCompUnit, Report report) {
        if (toCompUnit == null) {
            return;
        }

        Differences differences = new Differences(report);
        FileDiffs fileDiffs = differences.getFileDiffs();
        
        tr.Ace.log("fileDiffs", fileDiffs);
        
        Package pd = new Package(compUnit);
        pd.diff(toCompUnit, differences);
            
        Imports imps = new Imports(fileDiffs);
        imps.compare(compUnit, toCompUnit);

        Types types = new Types(compUnit);
        types.diff(toCompUnit, differences);
    }
}
