package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.ijdk.lang.*;
import org.incava.ijdk.util.*;
import org.incava.pmd.*;


public class TypeCtorDiff extends AbstractTypeItemDiff<ASTConstructorDeclaration> {

    public TypeCtorDiff(Collection<FileDiff> differences) {
        super(differences, ASTConstructorDeclaration.class);
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
        return TypeDiff.CONSTRUCTOR_ADDED;
    }

    public String getRemovedMessage(ASTConstructorDeclaration md) {
        return TypeDiff.CONSTRUCTOR_REMOVED;
    }
}
