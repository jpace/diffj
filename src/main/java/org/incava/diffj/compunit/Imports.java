package org.incava.diffj.compunit;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Differences;
import org.incava.diffj.Messages;
import org.incava.pmdx.CompilationUnitUtil;

public class Imports {
    private final ASTCompilationUnit compUnit;
    private final List<ASTImportDeclaration> imports;

    public Imports(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
        this.imports = CompilationUnitUtil.getImports(compUnit);
    }

    public void diff(Imports toImports, Differences differences) {
        if (isEmpty()) {
            if (!toImports.isEmpty()) {
                markAllAdded(toImports, differences);
            }
        }
        else if (toImports.isEmpty()) {
            markAllRemoved(toImports, differences);
        }
        else {
            compareEach(toImports, differences);
        }
    }

    public boolean isEmpty() {
        return imports.isEmpty();
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

    protected Collection<String> getNames() {
        Collection<String> names = new TreeSet<String>();
        for (ASTImportDeclaration imp : imports) {
            String str = getImportAsString(imp);
            names.add(str);
        }
        return names;
    }

    protected ASTImportDeclaration getDeclaration(String name) {
        for (ASTImportDeclaration imp : imports) {
            if (name.equals(getImportAsString(imp))) {
                return imp;
            }
        }
        return null;
    }

    protected void compareEach(Imports toImports, Differences differences) {
        Collection<String> names = getNames();
        names.addAll(toImports.getNames());

        for (String name : names) {
            ASTImportDeclaration fromImp = getDeclaration(name);
            ASTImportDeclaration toImp = toImports.getDeclaration(name);
            
            if (fromImp == null) {
                differences.added(getFirstDeclaration(), toImp, Messages.IMPORT_ADDED, name);
            }
            else if (toImp == null) {
                differences.deleted(fromImp, toImports.getFirstDeclaration(), Messages.IMPORT_REMOVED, name);
            }
        }
    }

    protected void markAllAdded(Imports toImports, Differences differences) {
        Token fromStart = getFirstTypeToken();
        Token fromEnd = fromStart;
        Token toStart = toImports.getFirstToken();
        Token toEnd = toImports.getLastToken();
        differences.added(fromStart, fromEnd, toStart, toEnd, Messages.IMPORT_SECTION_ADDED);
    }

    protected void markAllRemoved(Imports toImports, Differences differences) {
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
