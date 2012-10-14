package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;

public class InnerTypes extends Items<Type, ASTClassOrInterfaceDeclaration> {
    public InnerTypes(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration");
    }

    public Type getAstType(ASTClassOrInterfaceDeclaration decl) {
        return new Type(decl);
    }
}
