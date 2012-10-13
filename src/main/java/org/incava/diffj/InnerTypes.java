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
        return type.getName().image;
    }

    public String getAddedMessage(ASTClassOrInterfaceDeclaration decl) {
        return decl.isInterface() ? Messages.INNER_INTERFACE_ADDED : Messages.INNER_CLASS_ADDED;
    }

    public String getRemovedMessage(ASTClassOrInterfaceDeclaration decl) {
        return decl.isInterface() ? Messages.INNER_INTERFACE_REMOVED : Messages.INNER_CLASS_REMOVED;
    }
}
