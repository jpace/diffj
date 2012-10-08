package org.incava.diffj;

import java.util.Collection;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.CtorUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class TypeCtors extends TypeItems<ASTConstructorDeclaration> {
    private final FileDiffs fileDiffs;

    public TypeCtors(FileDiffs fileDiffs) {
        super(fileDiffs, "net.sourceforge.pmd.ast.ASTConstructorDeclaration");
        this.fileDiffs = fileDiffs;
    }    

    public void doCompare(ASTConstructorDeclaration a, ASTConstructorDeclaration b) {
        Ctors ctor = new Ctors(fileDiffs);
        ctor.compareAccess(SimpleNodeUtil.getParent(a), SimpleNodeUtil.getParent(b));
        ctor.compare(a, b);
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
