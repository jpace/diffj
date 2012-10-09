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

public class ImportsList {
    private final List<ASTImportDeclaration> imports;

    public ImportsList(ASTCompilationUnit compUnit) {
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
}
