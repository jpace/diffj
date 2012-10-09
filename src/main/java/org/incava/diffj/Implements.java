package org.incava.diffj;

import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;

/**
 * Compares implements.
 */
public class Implements extends Supers {
    public Implements(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
    }

    protected Map<String, ASTClassOrInterfaceType> getMap(ASTClassOrInterfaceDeclaration coid) {
        return getMap(coid, "net.sourceforge.pmd.ast.ASTImplementsList");
    }

    protected void superTypeChanged(ASTClassOrInterfaceType fromType, String fromName, ASTClassOrInterfaceType toDecl, String toName, Differences differences) {
        differences.changed(fromType, toDecl, Messages.IMPLEMENTED_TYPE_CHANGED, fromName, toName);
    }

    protected void superTypeAdded(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceType toType, String typeName, Differences differences) {
        differences.changed(fromDecl, toType, Messages.IMPLEMENTED_TYPE_ADDED, typeName);
    }

    protected void superTypeRemoved(ASTClassOrInterfaceType fromType, ASTClassOrInterfaceDeclaration toDecl, String typeName, Differences differences) {
        differences.changed(fromType, toDecl, Messages.IMPLEMENTED_TYPE_REMOVED, typeName);
    }
}
