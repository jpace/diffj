package org.incava.diffj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;

public class TypeMatches<ASTType extends Diffable<ASTType>, ItemType extends SimpleNode> {
    private final MultiMap<Double, Pair<ItemType, ItemType>> matches;
    private final Items<ASTType, ItemType> items;
    private final List<ItemType> unprocFromItems;
    private final List<ItemType> unprocToItems;
    private final List<ItemType> decls;

    public TypeMatches(Items<ASTType, ItemType> items, List<ItemType> decls) {
        this.items = items;
        this.matches = new MultiMap<Double, Pair<ItemType, ItemType>>();
        this.unprocFromItems = new ArrayList<ItemType>();
        this.unprocToItems = new ArrayList<ItemType>();
        this.decls = decls;
    }

    public List<ItemType> getUndiffedFromElements() {
        return this.unprocFromItems;
    }

    public List<ItemType> getUndiffedToElements() {
        return this.unprocToItems;
    }

    public void add(double score, ItemType firstType, ItemType secondType) {
        matches.put(score, Pair.create(firstType, secondType));
    }

    public Collection<Pair<ItemType, ItemType>> get(double score) {
        return matches.get(score);
    }

    public List<Double> getDescendingScores() {
        List<Double> descendingScores = new ArrayList<Double>(new TreeSet<Double>(matches.keySet()));
        Collections.reverse(descendingScores);
        return descendingScores;
    }

    public void diff(List<ItemType> toItems, Differences differences) {
        addMatches(toItems);
        compareMatches(toItems, differences);
    }

    public void addMatches(List<ItemType> toItems) {
        for (ItemType fromItem : decls) {
            ASTType fromType = items.getAstType(fromItem);
            for (ItemType toItem : toItems) {
                ASTType toType = items.getAstType(toItem);
                double matchScore = fromType.getMatchScore(toType);
                if (matchScore > 0.0) {
                    add(matchScore, fromItem, toItem);
                }
            }
        }
    }

    public void compareMatches(List<ItemType> toItems, Differences differences) {
        unprocFromItems.clear();
        unprocToItems.clear();

        unprocFromItems.addAll(decls);
        unprocToItems.addAll(toItems);

        List<Double> descendingScores = getDescendingScores();
        
        for (Double score : descendingScores) {
            // don't repeat comparisons ...
            diffAtScore(score, differences);
        }
    }

    private void diffAtScore(double score, Differences differences) {
        List<ItemType> procFromItems = new ArrayList<ItemType>();
        List<ItemType> procToItems = new ArrayList<ItemType>();

        for (Pair<ItemType, ItemType> declPair : get(score)) {
            ItemType fromItem = declPair.getFirst();
            ItemType toItem = declPair.getSecond();

            if (unprocFromItems.contains(fromItem) && unprocToItems.contains(toItem)) {
                ASTType fromType = items.getAstType(fromItem);
                ASTType toType = items.getAstType(toItem);

                fromType.diff(toType, differences);
                    
                procFromItems.add(fromItem);
                procToItems.add(toItem);
            }
        }

        unprocFromItems.removeAll(procFromItems);
        unprocToItems.removeAll(procToItems);
    }
}
