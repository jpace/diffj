package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import org.incava.pmdx.ClassUtil;

public class InnerTypes extends Items<ASTClassOrInterfaceDeclaration> {
    public InnerTypes(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration");
    }

    public void doCompare(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        Type fromType = new Type(fromDecl);
        Type toType = new Type(toDecl);
        fromType.diff(toType, differences);
    }

    public String getName(ASTClassOrInterfaceDeclaration coid) {
        return ClassUtil.getName(coid).image;
    }

    public String getAddedMessage(ASTClassOrInterfaceDeclaration coid) {
        return coid.isInterface() ? Messages.INNER_INTERFACE_ADDED : Messages.INNER_CLASS_ADDED;
    }

    public String getRemovedMessage(ASTClassOrInterfaceDeclaration coid) {
        return coid.isInterface() ? Messages.INNER_INTERFACE_REMOVED : Messages.INNER_CLASS_REMOVED;
    }

    public double getScore(ASTClassOrInterfaceDeclaration a, ASTClassOrInterfaceDeclaration b) {
        return ClassUtil.getMatchScore(a, b);
    }
}
