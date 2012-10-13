package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;

public class InnerTypes extends Items<Type, ASTClassOrInterfaceDeclaration> {
    public InnerTypes(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration");
    }

    public Type getAstType(ASTClassOrInterfaceDeclaration decl) {
        return new Type(decl);
    }

    public String getName(ASTClassOrInterfaceDeclaration decl) {
        Type type = new Type(decl);
        return type.getName();
    }

    public String getAddedMessage(ASTClassOrInterfaceDeclaration decl) {
        Type type = new Type(decl);
        return type.getAddedMessage();
    }

    public String getRemovedMessage(ASTClassOrInterfaceDeclaration decl) {
        Type type = new Type(decl);
        return type.getRemovedMessage();
    }
}
