package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import org.incava.pmdx.MethodUtil;

public class Methods extends Items<Method, ASTMethodDeclaration> {
    public Methods(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTMethodDeclaration");
    }    

    public Method getAstType(ASTMethodDeclaration methodDecl) {
        return new Method(methodDecl);
    }

    public String getName(ASTMethodDeclaration md) {
        return MethodUtil.getFullName(md);
    }

    public String getAddedMessage(ASTMethodDeclaration md) {
        return Messages.METHOD_ADDED;
    }

    public String getRemovedMessage(ASTMethodDeclaration md) {
        return Messages.METHOD_REMOVED;
    }
}
