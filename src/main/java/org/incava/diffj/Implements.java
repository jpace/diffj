package org.incava.diffj;

import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import org.incava.analysis.FileDiffs;

/**
 * Compares implements.
 */
public class Implements extends Supers {
    public Implements(FileDiffs differences) {
        super(differences);
    }

    public void compareImplements(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl) {
        compare(fromDecl, toDecl);
    }

    protected Map<String, ASTClassOrInterfaceType> getMap(ASTClassOrInterfaceDeclaration coid) {
        return getMap(coid, "net.sourceforge.pmd.ast.ASTImplementsList");
    }

    protected void superTypeChanged(ASTClassOrInterfaceType fromType, String fromName, ASTClassOrInterfaceType toDecl, String toName) {
        differences.changed(fromType, toDecl, Messages.IMPLEMENTED_TYPE_CHANGED, fromName, toName);
    }

    protected void superTypeAdded(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceType toType, String typeName) {
        differences.changed(fromDecl, toType, Messages.IMPLEMENTED_TYPE_ADDED, typeName);
    }

    protected void superTypeRemoved(ASTClassOrInterfaceType fromType, ASTClassOrInterfaceDeclaration toDecl, String typeName) {
        differences.changed(fromType, toDecl, Messages.IMPLEMENTED_TYPE_REMOVED, typeName);
    }
}
