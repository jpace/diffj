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
}
