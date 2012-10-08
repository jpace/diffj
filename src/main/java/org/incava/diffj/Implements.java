package org.incava.diffj;

import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import org.incava.analysis.FileDiffs;

/**
 * Compares implements.
 */
public class Implements extends Super {
    public Implements(FileDiffs differences) {
        super(differences);
    }

    public void compareImplements(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        compare(at, bt);
    }

    protected Map<String, ASTClassOrInterfaceType> getMap(ASTClassOrInterfaceDeclaration coid) {
        return getMap(coid, "net.sourceforge.pmd.ast.ASTImplementsList");
    }

    protected void superTypeChanged(ASTClassOrInterfaceType a, String aName, ASTClassOrInterfaceType b, String bName) {
        differences.changed(a, b, Messages.IMPLEMENTED_TYPE_CHANGED, aName, bName);
    }

    protected void superTypeAdded(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceType bType, String typeName) {
        differences.changed(at, bType, Messages.IMPLEMENTED_TYPE_ADDED, typeName);
    }

    protected void superTypeRemoved(ASTClassOrInterfaceType aType, ASTClassOrInterfaceDeclaration bt, String typeName) {
        differences.changed(aType, bt, Messages.IMPLEMENTED_TYPE_REMOVED, typeName);
    }
}
