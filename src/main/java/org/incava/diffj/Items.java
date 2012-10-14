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

    public abstract ASTType getAstType(ItemType item);

    public List<ASTType> toAstTypeList(List<ItemType> its) {
        List<ASTType> astList = new ArrayList<ASTType>();
        for (ItemType it : its) {
            astList.add(getAstType(it));
        }
        return astList;
    }

    public List<ASTType> getDeclarations() {
        List<ItemType> decls = type.getDeclarationsOfClassType(clsName);
        return toAstTypeList(decls);
    }

    public void diff(Items<ASTType, ItemType> toItems, Differences differences) {
        List<ASTType> fromTypes = getDeclarations();
        List<ASTType> toTypes = toItems.getDeclarations();

        TypeMatches<ASTType> matches = new TypeMatches<ASTType>(fromTypes);
        matches.diff(toTypes, differences);

        List<ASTType> removed = matches.getRemoved();
        List<ASTType> added = matches.getAdded();

        addRemoved(removed, toItems.type, differences);
        addAdded(added, differences);
    }

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
