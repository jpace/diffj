package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.MethodUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class TypeMethodDiff extends TypeItem<ASTMethodDeclaration> {
    private final FileDiffs fileDiffs;
    private final MethodUtil methodUtil;

    public TypeMethodDiff(FileDiffs fileDiffs) {
        super(fileDiffs, "net.sourceforge.pmd.ast.ASTMethodDeclaration");
        this.fileDiffs = fileDiffs;
        methodUtil = new MethodUtil();
    }    

    public void doCompare(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        MethodDiff differ = new MethodDiff(fileDiffs);
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
