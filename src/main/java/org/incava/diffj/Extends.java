package org.incava.diffj;

import java.util.Map;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import org.incava.analysis.FileDiffs;

/**
 * Compares extends.
 */
public class Extends extends Super {
    public Extends(FileDiffs differences) {
        super(differences);
    }

    public void compareExtends(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        compare(at, bt);
    }

    protected Map<String, ASTClassOrInterfaceType> getMap(ASTClassOrInterfaceDeclaration coid) {
        return getMap(coid, "net.sourceforge.pmd.ast.ASTExtendsList");
    }

    protected void superTypeChanged(ASTClassOrInterfaceType a, String aName, ASTClassOrInterfaceType b, String bName) {
        differences.changed(a, b, Messages.EXTENDED_TYPE_CHANGED, aName, bName);
    }

    protected void superTypeAdded(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceType bType, String typeName) {
        differences.changed(at, bType, Messages.EXTENDED_TYPE_ADDED, typeName);
    }

    protected void superTypeRemoved(ASTClassOrInterfaceType aType, ASTClassOrInterfaceDeclaration bt, String typeName) {
        differences.changed(aType, bt, Messages.EXTENDED_TYPE_REMOVED, typeName);
    }
}
