package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import org.incava.pmdx.CtorUtil;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.ParameterUtil;

public class Ctors extends Items<ASTConstructorDeclaration> {
    public Ctors(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTConstructorDeclaration");
    }    

    public void doCompare(ASTConstructorDeclaration fromCtorDecl, ASTConstructorDeclaration toCtorDecl, Differences differences) {
        Ctor fromCtor = new Ctor(fromCtorDecl);
        Ctor toCtor = new Ctor(toCtorDecl);
        fromCtor.diff(toCtor, differences);
    }

    public double getScore(ASTConstructorDeclaration fromDecl, ASTConstructorDeclaration toDecl) {
        Ctor fromCtor = new Ctor(fromDecl);
        Ctor toCtor = new Ctor(toDecl);

        Parameters fromParams = fromCtor.getParameters();
        Parameters toParams = toCtor.getParameters();

        return fromParams.getMatchScore(toParams);
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
