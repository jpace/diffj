package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;

public class InnerTypes extends Items<ASTClassOrInterfaceDeclaration> {
    public InnerTypes(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration");
    }

    public void doCompare(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        doCompare(new Type(fromDecl), new Type(toDecl), differences);
    }

    public void doCompare(Type fromType, Type toType, Differences differences) {
        fromType.diff(toType, differences);
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

    public double getScore(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl) {
        Type fromType = new Type(fromDecl);
        Type toType = new Type(toDecl);
        return fromType.getMatchScore(toType);
    }
}
