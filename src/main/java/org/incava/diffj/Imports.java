package org.incava.diffj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Imports {
    private final ASTCompilationUnit compUnit;
    private final ImportsList imports;

    public Imports(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
        this.imports = new ImportsList(compUnit);
    }

    public void diff(ASTCompilationUnit toCompUnit, Differences differences) {
        ImportsList toImports = new ImportsList(toCompUnit);

        if (imports.isEmpty()) {
            if (!toImports.isEmpty()) {
                markImportSectionAdded(toImports, differences);
            }
        }
        else if (toImports.isEmpty()) {
            markImportSectionRemoved(toCompUnit, differences);
        }
        else {
            compareEachImport(toImports, differences);
        }
    }

    protected void compareEachImport(ImportsList toImports, Differences differences) {
        Map<String, ASTImportDeclaration> fromNamesToImp = imports.getNamesToDeclarations();
        Map<String, ASTImportDeclaration> toNamesToImp = toImports.getNamesToDeclarations();
            
        Collection<String> names = new TreeSet<String>();
        names.addAll(fromNamesToImp.keySet());
        names.addAll(toNamesToImp.keySet());

        for (String name : names) {
            ASTImportDeclaration fromImp = fromNamesToImp.get(name);
            ASTImportDeclaration toImp = toNamesToImp.get(name);
            
            if (fromImp == null) {
                differences.added(imports.getFirstDeclaration(), toImp, Messages.IMPORT_ADDED, name);
            }
            else if (toImp == null) {
                differences.deleted(fromImp, toImports.getFirstDeclaration(), Messages.IMPORT_REMOVED, name);
            }
        }
    }

    protected void markImportSectionAdded(ImportsList toImports, Differences differences) {
        Token fromStart = getFirstTypeToken(compUnit);
        Token fromEnd = fromStart;
        Token toStart = toImports.getFirstToken();
        Token toEnd = toImports.getLastToken();
        differences.added(fromStart, fromEnd, toStart, toEnd, Messages.IMPORT_SECTION_ADDED);
    }

    protected void markImportSectionRemoved(ASTCompilationUnit toCompUnit, Differences differences) {
        Token fromStart = imports.getFirstToken();
        Token fromEnd = imports.getLastToken();
        Token toStart = getFirstTypeToken(toCompUnit);
        Token toEnd = toStart;
        differences.deleted(fromStart, fromEnd, toStart, toEnd, Messages.IMPORT_SECTION_REMOVED);
    }

    protected Token getFirstTypeToken(ASTCompilationUnit cu) {
        List<ASTTypeDeclaration> types = CompilationUnitUtil.getTypeDeclarations(cu);
        Token t = types.size() > 0 ? types.get(0).getFirstToken() : null;

        // if there are no types (ie. the file has only a package and/or import
        // statements), then just point to the first token in the compilation
        // unit.

        if (t == null) {
            t = cu.getFirstToken();
        }
        return t;
    }
}
