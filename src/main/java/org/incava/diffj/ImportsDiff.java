package org.incava.diffj;

import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.pmd.*;


public class ImportsDiff extends DiffComparator {
    public static final String IMPORT_REMOVED = "import removed: {0}";

    public static final String IMPORT_ADDED = "import added: {0}";

    public static final String IMPORT_SECTION_REMOVED = "import section removed";

    public static final String IMPORT_SECTION_ADDED = "import section added";

    public ImportsDiff(Collection<FileDiff> differences) {
        super(differences);
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        ASTImportDeclaration[] aImports = CompilationUnitUtil.getImports(a);
        ASTImportDeclaration[] bImports = CompilationUnitUtil.getImports(b);

        if (aImports.length == 0) {
            if (bImports.length == 0) {
                // tr.Ace.log("neither has imports section");
            }
            else {
                Token a0 = getFirstTypeToken(a);
                Token a1 = a0;
                Token b0 = getFirstToken(bImports);
                Token b1 = getLastToken(bImports);
                added(a0, a1, b0, b1, IMPORT_SECTION_ADDED);
            }
        }
        else if (bImports.length == 0) {
            Token a0 = getFirstToken(aImports);
            Token a1 = getLastToken(aImports);
            Token b0 = getFirstTypeToken(b);
            Token b1 = b0;
            deleted(a0, a1, b0, b1, IMPORT_SECTION_REMOVED);
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
                    added(aImports[0], bimp, IMPORT_ADDED, name);
                }
                else if (bimp == null) {
                    deleted(aimp, bImports[0], IMPORT_REMOVED, name);
                }
                else {
                    // tr.Ace.log("no change");
                }
            }
        }
    }

    protected Map<String, ASTImportDeclaration> makeImportMap(ASTImportDeclaration[] imports) {
        Map<String, ASTImportDeclaration> namesToImp = new HashMap<String, ASTImportDeclaration>();

        for (int ii = 0; ii < imports.length; ++ii) {
            ASTImportDeclaration imp = imports[ii];
            StringBuffer         buf = new StringBuffer();   
            Token                tk  = imp.getFirstToken().next;
            
            while (tk != null) {
                if (tk == imp.getLastToken()) {
                    break;
                }
                else {
                    buf.append(tk.image);
                    tk = tk.next;
                }
            }

            namesToImp.put(buf.toString(), imp);
        }
        
        return namesToImp;
    }

    protected Token getFirstToken(ASTImportDeclaration[] imports) {
        return imports[0].getFirstToken();
    }

    protected Token getLastToken(ASTImportDeclaration[] imports) {
        return imports[imports.length - 1].getLastToken();
    }

    protected Token getFirstTypeToken(ASTCompilationUnit cu) {
        ASTTypeDeclaration[] types = CompilationUnitUtil.getTypeDeclarations(cu);
        Token t = types.length > 0 ? types[0].getFirstToken() : null;

        // if there are no types (ie. the file has only a package and/or import
        // statements), then just point to the first token in the compilation
        // unit.

        if (t == null) {
            t = cu.getFirstToken();
        }
        return t;
    }
}
