package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

public class Methods extends Items<Method, ASTMethodDeclaration> {
    public Methods(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTMethodDeclaration");
    }    

    public Method getAstType(ASTMethodDeclaration methodDecl) {
        return new Method(methodDecl);
    }

    public String getName(ASTMethodDeclaration methodDecl) {
        Method meth = new Method(methodDecl);
        return meth.getName();
    }

    public String getAddedMessage(ASTMethodDeclaration methodDecl) {
        Method meth = new Method(methodDecl);
        return meth.getAddedMessage();
    }

    public String getRemovedMessage(ASTMethodDeclaration methodDecl) {
        Method meth = new Method(methodDecl);
        return meth.getRemovedMessage();
    }
}
