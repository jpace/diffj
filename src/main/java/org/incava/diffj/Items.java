package org.incava.diffj;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

/**
 * Items represents the methods, ctors, fields and inner types of a parent type.
 */
public abstract class Items<ASTType extends Diffable<ASTType>, ItemType extends SimpleNode> {
    private final String clsName;
    private final Type type;

    public Items(ASTClassOrInterfaceDeclaration decl, String clsName) {
        this.type = new Type(decl);
        this.clsName = clsName;
    }

    public void diff(Items<ASTType, ItemType> toItems, Differences differences) {
    }

    public List<ASTType> toAstTypeList(List<ItemType> its) {
        List<ASTType> astList = new ArrayList<ASTType>();
        for (ItemType it : its) {
            astList.add(getAstType(it));
        }
        return astList;
    }

    public void diff(Type toType, Differences differences) {
        List<ItemType> fromDecls = type.getDeclarationsOfClassType(clsName);
        List<ItemType> toDecls = toType.getDeclarationsOfClassType(clsName);

        List<ASTType> fromTypes = toAstTypeList(fromDecls);
        List<ASTType> toTypes = toAstTypeList(toDecls);

        TypeMatches<ASTType> matches = new TypeMatches<ASTType>(fromTypes);
        matches.diff(toTypes, differences);

        List<ASTType> removed = matches.getRemoved();
        List<ASTType> added = matches.getAdded();

        addRemoved(removed, toType, differences);
        addAdded(added, differences);
    }

    public abstract ASTType getAstType(ItemType item);

    public void addAdded(List<ASTType> added, Differences differences) {
        for (ASTType toAdd : added) {
            String name = toAdd.getName();
            differences.added(type.getDeclaration(), toAdd.getNode(), toAdd.getAddedMessage(), name);
        }
    }

    public void addRemoved(List<ASTType> removed, Type toType, Differences differences) {
        for (ASTType goner : removed) {
            String name = goner.getName();
            differences.deleted(goner.getNode(), toType.getDeclaration(), goner.getRemovedMessage(), name);
        }
    }
}
