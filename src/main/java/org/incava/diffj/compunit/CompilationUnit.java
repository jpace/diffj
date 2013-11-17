package org.incava.diffj.compunit;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import org.incava.analysis.Report;
import org.incava.diffj.element.Differences;
import org.incava.diffj.type.Types;

public class CompilationUnit {
    private final ASTCompilationUnit compUnit;

    public CompilationUnit(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
    }

    public Package getPackage() {
        return new Package(compUnit);
    }

    public Imports getImports() {
        return new Imports(compUnit);
    }

    public Types getTypes() {
        return new Types(compUnit);
    }

    public ASTCompilationUnit getAstCompUnit() {
        return compUnit;
    }

    public void diff(CompilationUnit toCompUnit, Report report) {
        if (toCompUnit == null) {
            return;
        }

        Differences differences = new Differences(report);
        
        Package fromPackage = getPackage();
        Package toPackage = toCompUnit.getPackage();
        fromPackage.diff(toPackage, differences);
            
        Imports fromImports = getImports();
        Imports toImports = toCompUnit.getImports();
        fromImports.diff(toImports, differences);

        Types fromTypes = getTypes();
        Types toTypes = toCompUnit.getTypes();
        fromTypes.diff(toTypes, differences);
    }
}
