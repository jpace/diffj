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
        
        Package fromPackage = new Package(compUnit);
        Package toPackage = new Package(toCompUnit);
        fromPackage.diff(toPackage, differences);
            
        Imports fromImports = new Imports(compUnit);
        Imports toImports = new Imports(toCompUnit);
        fromImports.diff(toImports, differences);

        Types fromTypes = new Types(compUnit);
        Types toTypes = new Types(toCompUnit);
        fromTypes.diff(toTypes, differences);
    }
}
