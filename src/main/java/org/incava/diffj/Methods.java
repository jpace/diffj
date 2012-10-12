package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import org.incava.pmdx.MethodUtil;

public class Methods extends Items<ASTMethodDeclaration> {
    private final MethodUtil methodUtil;

    public Methods(ASTClassOrInterfaceDeclaration type) {
        super(type, "net.sourceforge.pmd.ast.ASTMethodDeclaration");
        methodUtil = new MethodUtil();
    }    

    public void doCompare(ASTMethodDeclaration fromMethodDecl, ASTMethodDeclaration toMethodDecl, Differences differences) {
        Method fromMethod = new Method(fromMethodDecl);
        Method toMethod = new Method(toMethodDecl);
        fromMethod.diff(toMethod, differences);
    }

    public double getScore(ASTMethodDeclaration fromMethodDecl, ASTMethodDeclaration toMethodDecl) {
        Method fromMethod = new Method(fromMethodDecl);
        Method toMethod = new Method(toMethodDecl);
        return fromMethod.getMatchScore(toMethod);
    }

    public String getName(ASTMethodDeclaration md) {
        return MethodUtil.getFullName(md);
    }

    public String getAddedMessage(ASTMethodDeclaration md) {
        return Messages.METHOD_ADDED;
    }

    public String getRemovedMessage(ASTMethodDeclaration md) {
        return Messages.METHOD_REMOVED;
    }
}
