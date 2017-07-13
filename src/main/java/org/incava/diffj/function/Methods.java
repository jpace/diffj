package org.incava.diffj.function;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import org.incava.diffj.type.Items;

public class Methods extends Items<Method, ASTMethodDeclaration> {
    public Methods(ASTClassOrInterfaceDeclaration typeDecl) {
        super(typeDecl, ASTMethodDeclaration.class);
    }

    public Method getAstType(ASTMethodDeclaration methodDecl) {
        return new Method(methodDecl);
    }
}
