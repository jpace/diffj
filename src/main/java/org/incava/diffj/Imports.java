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
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Imports {
    private final ASTCompilationUnit compUnit;

    public Imports(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void diff(ASTCompilationUnit toCompUnit, Differences differences) {
        List<ASTImportDeclaration> fromImports = CompilationUnitUtil.getImports(compUnit);
        List<ASTImportDeclaration> toImports = CompilationUnitUtil.getImports(toCompUnit);

        if (fromImports.size() == 0) {
            if (toImports.size() != 0) {
                markImportSectionAdded(toImports, differences);
            }
        }
        else if (toImports.size() == 0) {
            markImportSectionRemoved(fromImports, toCompUnit, differences);
        }
        else {
            Map<String, ASTImportDeclaration> fromNamesToImp = makeImportMap(fromImports);
            Map<String, ASTImportDeclaration> toNamesToImp = makeImportMap(toImports);
            
            Collection<String> names = new TreeSet<String>();
            names.addAll(fromNamesToImp.keySet());
            names.addAll(toNamesToImp.keySet());

            for (String name : names) {
                ASTImportDeclaration fromImp = fromNamesToImp.get(name);
                ASTImportDeclaration toImp = toNamesToImp.get(name);
            
                if (fromImp == null) {
                    differences.added(fromImports.get(0), toImp, Messages.IMPORT_ADDED, name);
                }
                else if (toImp == null) {
                    differences.deleted(fromImp, toImports.get(0), Messages.IMPORT_REMOVED, name);
                }
            }
        }
    }

    protected void markImportSectionAdded(List<ASTImportDeclaration> toImports, Differences differences) {
        Token fromStart = getFirstTypeToken(compUnit);
        Token fromEnd = fromStart;
        Token toStart = getFirstToken(toImports);
        Token toEnd = getLastToken(toImports);
        differences.added(fromStart, fromEnd, toStart, toEnd, Messages.IMPORT_SECTION_ADDED);
    }

    protected void markImportSectionRemoved(List<ASTImportDeclaration> fromImports, ASTCompilationUnit toCompUnit, Differences differences) {
        Token fromStart = getFirstToken(fromImports);
        Token fromEnd = getLastToken(fromImports);
        Token toStart = getFirstTypeToken(toCompUnit);
        Token toEnd = toStart;
        differences.deleted(fromStart, fromEnd, toStart, toEnd, Messages.IMPORT_SECTION_REMOVED);
    }

    protected String getImportAsString(ASTImportDeclaration imp) {
        StringBuilder sb = new StringBuilder();   
        Token tk  = imp.getFirstToken().next;
        
        while (tk != null) {
            if (tk == imp.getLastToken()) {
                break;
            }
            else {
                sb.append(tk.image);
                tk = tk.next;
            }
        }

        return sb.toString();
    }

    protected Map<String, ASTImportDeclaration> makeImportMap(List<ASTImportDeclaration> imports) {
        Map<String, ASTImportDeclaration> namesToImp = new HashMap<String, ASTImportDeclaration>();

        for (ASTImportDeclaration imp : imports) {
            String str = getImportAsString(imp);
            namesToImp.put(str, imp);
        }
        
        return namesToImp;
    }

    protected Token getFirstToken(List<ASTImportDeclaration> imports) {
        return imports.get(0).getFirstToken();
    }

    protected Token getLastToken(List<ASTImportDeclaration> imports) {
        return imports.get(imports.size() - 1).getLastToken();
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
