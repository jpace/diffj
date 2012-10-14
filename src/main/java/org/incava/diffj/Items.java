package org.incava.diffj;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;

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

    public void diff(Type toType, Differences differences) {
        List<ItemType> fromDecls = type.getDeclarationsOfClassType(clsName);
        List<ItemType> toDecls = toType.getDeclarationsOfClassType(clsName);

        TypeMatches<ASTType, ItemType> matches = new TypeMatches<ASTType, ItemType>(this, fromDecls);
        matches.addMatches(toDecls);

        matches.compareMatches(toDecls, differences);

        List<ItemType> unprocFromDecls = matches.getUndiffedFromElements();
        List<ItemType> unprocToDecls = matches.getUndiffedToElements();

        addRemoved(unprocFromDecls, toType, differences);
        addAdded(unprocToDecls, differences);
    }

    public abstract ASTType getAstType(ItemType item);

    public void addAdded(List<ItemType> toItems, Differences differences) {
        for (ItemType toItem : toItems) {
            ASTType toType = getAstType(toItem);
            String name = toType.getName();
            differences.added(type.getDeclaration(), toItem, toType.getAddedMessage(), name);
        }
    }

    public void addRemoved(List<ItemType> fromItems, Type toType, Differences differences) {
        for (ItemType fromItem : fromItems) {
            tr.Ace.log("fromItem", fromItem);
            ASTType fromType = getAstType(fromItem);
            String name = fromType.getName();
            differences.deleted(fromItem, toType.getDeclaration(), fromType.getRemovedMessage(), name);
        }
    }
}
