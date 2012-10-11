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
public abstract class Items<ItemType extends SimpleNode> {
    private final String clsName;
    private final Type type;

    public Items(ASTClassOrInterfaceDeclaration decl, String clsName) {
        this.type = new Type(decl);
        this.clsName = clsName;
    }

    public void diff(Items<ItemType> toItems, Differences differences) {
    }

    public void diff(Type toType, Differences differences) {
        List<ItemType> fromDecls = type.getDeclarationsOfClassType(clsName);
        List<ItemType> toDecls = toType.getDeclarationsOfClassType(clsName);

        TypeMatches<ItemType> matches = getTypeMatches(fromDecls, toDecls);

        List<ItemType> unprocFromDecls = new ArrayList<ItemType>(fromDecls);
        List<ItemType> unprocToDecls = new ArrayList<ItemType>(toDecls);

        compareMatches(matches, unprocFromDecls, unprocToDecls, differences);

        addRemoved(unprocFromDecls, toType, differences);
        addAdded(unprocToDecls, differences);
    }

    public abstract String getName(ItemType item);

    public abstract String getAddedMessage(ItemType item);

    public abstract String getRemovedMessage(ItemType item);

    public abstract double getScore(ItemType fromItem, ItemType toItem);

    public abstract void doCompare(ItemType fromItem, ItemType toItem, Differences differences);

    public TypeMatches<ItemType> getTypeMatches(List<ItemType> fromItems, List<ItemType> toItems) {
        TypeMatches<ItemType> matches = new TypeMatches<ItemType>();

        for (ItemType fromItem : fromItems) {
            for (ItemType toItem : toItems) {
                double score = getScore(fromItem, toItem);
                if (score > 0.0) {
                    matches.add(score, fromItem, toItem);
                }
            }
        }
        return matches;
    }

    public void compareMatches(TypeMatches<ItemType> matches, List<ItemType> unprocFromItems, List<ItemType> unprocToItems, Differences differences) {
        List<Double> descendingScores = matches.getDescendingScores();
        
        for (Double score : descendingScores) {
            // don't repeat comparisons ...

            List<ItemType> procFromItems = new ArrayList<ItemType>();
            List<ItemType> procToItems = new ArrayList<ItemType>();

            for (Pair<ItemType, ItemType> declPair : matches.get(score)) {
                ItemType fromItem = declPair.getFirst();
                ItemType toItem = declPair.getSecond();

                if (unprocFromItems.contains(fromItem) && unprocToItems.contains(toItem)) {
                    doCompare(fromItem, toItem, differences);
                    
                    procFromItems.add(fromItem);
                    procToItems.add(toItem);
                }
            }

            unprocFromItems.removeAll(procFromItems);
            unprocToItems.removeAll(procToItems);
        }
    }

    public void addAdded(List<ItemType> toItems, Differences differences) {
        for (ItemType toItem : toItems) {
            String name = getName(toItem);
            differences.added(type.getDeclaration(), toItem, getAddedMessage(toItem), name);
        }
    }

    public void addRemoved(List<ItemType> fromItems, Type toType, Differences differences) {
        for (ItemType fromItem : fromItems) {
            String name = getName(fromItem);
            differences.deleted(fromItem, toType.getDeclaration(), getRemovedMessage(fromItem), name);
        }
    }
}
