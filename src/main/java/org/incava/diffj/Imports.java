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
    private final Differences differences;

    public Imports(FileDiffs fileDiffs) {
        this.differences = new Differences(fileDiffs);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        List<ASTImportDeclaration> aImports = CompilationUnitUtil.getImports(a);
        List<ASTImportDeclaration> bImports = CompilationUnitUtil.getImports(b);

        if (aImports.size() == 0) {
            if (bImports.size() != 0) {
                markImportSectionAdded(a, bImports);
            }
        }
        else if (bImports.size() == 0) {
            markImportSectionRemoved(aImports, b);
        }
        else {
            Map<String, ASTImportDeclaration> aNamesToImp = makeImportMap(aImports);
            Map<String, ASTImportDeclaration> bNamesToImp = makeImportMap(bImports);
            
            Collection<String> names = new TreeSet<String>();
            names.addAll(aNamesToImp.keySet());
            names.addAll(bNamesToImp.keySet());

            for (String name : names) {
                ASTImportDeclaration aimp = aNamesToImp.get(name);
                ASTImportDeclaration bimp = bNamesToImp.get(name);
            
                if (aimp == null) {
                    differences.added(aImports.get(0), bimp, Messages.IMPORT_ADDED, name);
                }
                else if (bimp == null) {
                    differences.deleted(aimp, bImports.get(0), Messages.IMPORT_REMOVED, name);
                }
            }
        }
    }

    protected void markImportSectionAdded(ASTCompilationUnit a, List<ASTImportDeclaration> bImports) {
        Token a0 = getFirstTypeToken(a);
        Token a1 = a0;
        Token b0 = getFirstToken(bImports);
        Token b1 = getLastToken(bImports);
        differences.added(a0, a1, b0, b1, Messages.IMPORT_SECTION_ADDED);
    }

    protected void markImportSectionRemoved(List<ASTImportDeclaration> aImports, ASTCompilationUnit b) {
        Token a0 = getFirstToken(aImports);
        Token a1 = getLastToken(aImports);
        Token b0 = getFirstTypeToken(b);
        Token b1 = b0;
        differences.deleted(a0, a1, b0, b1, Messages.IMPORT_SECTION_REMOVED);
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
