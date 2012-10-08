package org.incava.diffj;

import java.util.Collection;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.CtorUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class TypeCtorDiff extends TypeItem<ASTConstructorDeclaration> {
    public TypeCtorDiff(FileDiffs differences) {
        super(differences, "net.sourceforge.pmd.ast.ASTConstructorDeclaration");
    }    

    public void doCompare(ASTConstructorDeclaration a, ASTConstructorDeclaration b) {
        CtorDiff differ = new CtorDiff(getFileDiffs());
        differ.compareAccess(SimpleNodeUtil.getParent(a), SimpleNodeUtil.getParent(b));
        differ.compare(a, b);
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
