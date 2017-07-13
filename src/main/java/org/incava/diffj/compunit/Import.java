package org.incava.diffj.compunit;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.Node;

public class Import {
    private final ASTImportDeclaration imp;

    public Import(ASTImportDeclaration imp) {
        this.imp = imp;
    }

    public ASTImportDeclaration getNode() {
        return imp;
    }

    public Token getFirstToken() {
        return Node.of(imp).getFirstToken();
    }

    public Token getLastToken() {
        return Node.of(imp).getLastToken();
    }

    public String getAsString() {
        StringBuilder sb = new StringBuilder();   
        Token tk  = Node.of(imp).getFirstToken().next;
        
        while (tk != null) {
            if (tk == Node.of(imp).getLastToken()) {
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
