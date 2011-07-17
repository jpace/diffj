package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.ijdk.lang.*;
import org.incava.ijdk.util.*;
import org.incava.pmd.*;


public class TypeInnerTypeDiff extends AbstractTypeItemDiff<ASTClassOrInterfaceDeclaration> {

    private final TypeDiff typeDiff;

    public TypeInnerTypeDiff(Collection<FileDiff> differences, TypeDiff typeDiff) {
        super(differences, ASTClassOrInterfaceDeclaration.class);

        this.typeDiff = typeDiff;
    }

    public void doCompare(ASTClassOrInterfaceDeclaration a, ASTClassOrInterfaceDeclaration b) {
        typeDiff.compare(a, b);
    }

    public String getName(ASTClassOrInterfaceDeclaration coid) {
        return ClassUtil.getName(coid).image;
    }

    public String getAddedMessage(ASTClassOrInterfaceDeclaration coid) {
        return coid.isInterface() ? TypeDiff.INNER_INTERFACE_ADDED : TypeDiff.INNER_CLASS_ADDED;
    }

    public String getRemovedMessage(ASTClassOrInterfaceDeclaration coid) {
        return coid.isInterface() ? TypeDiff.INNER_INTERFACE_REMOVED : TypeDiff.INNER_CLASS_REMOVED;
    }

    public double getScore(ASTClassOrInterfaceDeclaration a, ASTClassOrInterfaceDeclaration b) {
        return ClassUtil.getMatchScore(a, b);
    }
}
