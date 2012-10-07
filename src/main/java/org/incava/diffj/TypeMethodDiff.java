package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.MethodUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class TypeMethodDiff extends AbstractTypeItemDiff<ASTMethodDeclaration> {
    private final MethodUtil methodUtil;

    public TypeMethodDiff(FileDiffs differences) {
        super(differences, "net.sourceforge.pmd.ast.ASTMethodDeclaration");        
        methodUtil = new MethodUtil();
    }    

    public void doCompare(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        MethodDiff differ = new MethodDiff(getFileDiffs());
        differ.compareAccess(SimpleNodeUtil.getParent(a), SimpleNodeUtil.getParent(b));
        differ.compare(a, b);
    }

    public double getScore(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        return MethodUtil.getMatchScore(a, b);
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
