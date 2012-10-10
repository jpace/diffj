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
    private final List<ASTImportDeclaration> imports;

    public Imports(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
        this.imports = CompilationUnitUtil.getImports(compUnit);
    }

    public boolean isEmpty() {
        return imports.isEmpty();
    }

    public Map<String, ASTImportDeclaration> getNamesToDeclarations() {
        Map<String, ASTImportDeclaration> namesToImp = new HashMap<String, ASTImportDeclaration>();
        for (ASTImportDeclaration imp : imports) {
            String str = getImportAsString(imp);
            namesToImp.put(str, imp);
        }
        
        return namesToImp;
    }        

    public ASTImportDeclaration getFirstDeclaration() {
        return imports.get(0);
    }

    public Token getFirstToken() {
        return imports.get(0).getFirstToken();
    }

    public Token getLastToken() {
        return imports.get(imports.size() - 1).getLastToken();
    }

    public String getImportAsString(ASTImportDeclaration imp) {
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


    public void diff(Imports toImports, Differences differences) {
        if (isEmpty()) {
            if (!toImports.isEmpty()) {
                markImportSectionAdded(toImports, differences);
            }
        }
        else if (toImports.isEmpty()) {
            markImportSectionRemoved(toImports, differences);
        }
        else {
            compareEachImport(toImports, differences);
        }
    }

    protected void compareEachImport(Imports toImports, Differences differences) {
        Map<String, ASTImportDeclaration> fromNamesToImp = getNamesToDeclarations();
        Map<String, ASTImportDeclaration> toNamesToImp = toImports.getNamesToDeclarations();
            
        Collection<String> names = new TreeSet<String>();
        names.addAll(fromNamesToImp.keySet());
        names.addAll(toNamesToImp.keySet());

        for (String name : names) {
            ASTImportDeclaration fromImp = fromNamesToImp.get(name);
            ASTImportDeclaration toImp = toNamesToImp.get(name);
            
            if (fromImp == null) {
                differences.added(getFirstDeclaration(), toImp, Messages.IMPORT_ADDED, name);
            }
            else if (toImp == null) {
                differences.deleted(fromImp, toImports.getFirstDeclaration(), Messages.IMPORT_REMOVED, name);
            }
        }
    }

    protected void markImportSectionAdded(Imports toImports, Differences differences) {
        Token fromStart = getFirstTypeToken();
        Token fromEnd = fromStart;
        Token toStart = toImports.getFirstToken();
        Token toEnd = toImports.getLastToken();
        differences.added(fromStart, fromEnd, toStart, toEnd, Messages.IMPORT_SECTION_ADDED);
    }

    protected void markImportSectionRemoved(Imports toImports, Differences differences) {
        Token fromStart = getFirstToken();
        Token fromEnd = getLastToken();
        Token toStart = toImports.getFirstTypeToken();
        Token toEnd = toStart;
        differences.deleted(fromStart, fromEnd, toStart, toEnd, Messages.IMPORT_SECTION_REMOVED);
    }

    protected Token getFirstTypeToken() {
        List<ASTTypeDeclaration> types = CompilationUnitUtil.getTypeDeclarations(compUnit);
        Token t = types.size() > 0 ? types.get(0).getFirstToken() : null;

        // if there are no types (ie. the file has only a package and/or import
        // statements), then just point to the first token in the compilation
        // unit.

        if (t == null) {
            t = compUnit.getFirstToken();
        }
        return t;
    }
}
