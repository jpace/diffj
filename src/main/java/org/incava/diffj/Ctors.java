package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import org.incava.pmdx.CtorUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Ctors extends Items<ASTConstructorDeclaration> {
    public Ctors(ASTClassOrInterfaceDeclaration type, Differences differences) {
        super(type, "net.sourceforge.pmd.ast.ASTConstructorDeclaration", differences);
    }    

    public void doCompare(ASTConstructorDeclaration fromCtor, ASTConstructorDeclaration toCtor) {
        Ctor ctor = new Ctor(fromCtor);
        ctor.compareAccess(SimpleNodeUtil.getParent(fromCtor), SimpleNodeUtil.getParent(toCtor), differences);
        ctor.diff(toCtor, differences);
    }

    public double getScore(ASTConstructorDeclaration a, ASTConstructorDeclaration b) {
        return CtorUtil.getMatchScore(a, b);
    }

    public String getName(ASTConstructorDeclaration md) {
        return CtorUtil.getFullName(md);
    }

    public String getAddedMessage(ASTConstructorDeclaration md) {
        return Messages.CONSTRUCTOR_ADDED;
    }

    public String getRemovedMessage(ASTConstructorDeclaration md) {
        return Messages.CONSTRUCTOR_REMOVED;
    }
}
