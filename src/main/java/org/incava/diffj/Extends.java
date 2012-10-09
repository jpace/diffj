package org.incava.diffj;

import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import org.incava.analysis.FileDiffs;

/**
 * Compares extends.
 */
public class Extends extends Supers {
    public Extends(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
    }

    protected Map<String, ASTClassOrInterfaceType> getMap(ASTClassOrInterfaceDeclaration coid) {
        return getMap(coid, "net.sourceforge.pmd.ast.ASTExtendsList");
    }

    protected void superTypeChanged(ASTClassOrInterfaceType fromType, String fromName, ASTClassOrInterfaceType toType, String toName, Differences differences) {
        differences.changed(fromType, toType, Messages.EXTENDED_TYPE_CHANGED, fromName, toName);
    }

    protected void superTypeAdded(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceType toType, String typeName, Differences differences) {
        differences.changed(fromDecl, toType, Messages.EXTENDED_TYPE_ADDED, typeName);
    }

    protected void superTypeRemoved(ASTClassOrInterfaceType fromType, ASTClassOrInterfaceDeclaration toDecl, String typeName, Differences differences) {
        differences.changed(fromType, toDecl, Messages.EXTENDED_TYPE_REMOVED, typeName);
    }
}
