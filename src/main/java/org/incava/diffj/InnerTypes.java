package org.incava.diffj;

import java.util.Collection;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.ClassUtil;

public class InnerTypes extends TypeItems<ASTClassOrInterfaceDeclaration> {
    private final Type typeDiff;

    public InnerTypes(FileDiffs differences, Type typeDiff) {
        super(differences, "net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration");
        this.typeDiff = typeDiff;
    }

    public void doCompare(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl) {
        Type type = new Type(fromDecl);
        typeDiff.diff(fromDecl, toDecl, differences);
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
