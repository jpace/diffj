package org.incava.diffj.type;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;

public class InnerTypes extends Items<Type, ASTClassOrInterfaceDeclaration> {
    public InnerTypes(ASTClassOrInterfaceDeclaration typeDecl) {
        super(typeDecl, ASTClassOrInterfaceDeclaration.class);
    }

    public Type getAstType(ASTClassOrInterfaceDeclaration typeDecl) {
        return new Type(typeDecl);
    }
}
