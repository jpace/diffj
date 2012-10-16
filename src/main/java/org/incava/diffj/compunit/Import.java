package org.incava.diffj.compunit;

import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;

public class Import {
    private final ASTImportDeclaration imp;

    public Import(ASTImportDeclaration imp) {
        this.imp = imp;
    }

    public ASTImportDeclaration getNode() {
        return imp;
    }

    public Token getFirstToken() {
        return imp.getFirstToken();
    }

    public Token getLastToken() {
        return imp.getLastToken();
    }

    public String getAsString() {
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
