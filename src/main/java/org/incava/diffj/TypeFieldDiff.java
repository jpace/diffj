package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.ijdk.lang.*;
import org.incava.ijdk.util.*;
import org.incava.pmd.*;


public class TypeFieldDiff extends AbstractTypeItemDiff<ASTFieldDeclaration> {

    public TypeFieldDiff(Collection<FileDiff> differences) {
        super(differences, ASTFieldDeclaration.class);
    }    

    public void doCompare(ASTFieldDeclaration a, ASTFieldDeclaration b) {
        FieldDiff differ = new FieldDiff(getFileDiffs());
        differ.compareAccess(SimpleNodeUtil.getParent(a), SimpleNodeUtil.getParent(b));
        differ.compare(a, b);
    }

    public String getName(ASTFieldDeclaration fd) {
        return FieldUtil.getNames(fd);
    }

    public String getAddedMessage(ASTFieldDeclaration fd) {
        return TypeDiff.FIELD_ADDED;
    }

    public String getRemovedMessage(ASTFieldDeclaration fd) {
        return TypeDiff.FIELD_REMOVED;
    }

    public double getScore(ASTFieldDeclaration a, ASTFieldDeclaration b) {
        return FieldUtil.getMatchScore(a, b);
    }
}
