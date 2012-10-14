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
    private final MultiMap<Double, Pair<ASTType, ASTType>> matches;
    private final Items<ASTType, ItemType> items;
    private final List<ASTType> unprocFromItems;
    private final List<ASTType> unprocToItems;
    private final List<ASTType> decls;

    public TypeMatches(Items<ASTType, ItemType> items, List<ItemType> decls) {
        this.items = items;
        this.matches = new MultiMap<Double, Pair<ASTType, ASTType>>();
        this.unprocFromItems = new ArrayList<ASTType>();
        this.unprocToItems = new ArrayList<ASTType>();
        this.decls = toAstTypeList(decls);
    }

    public List<ASTType> getRemoved() {
        return unprocFromItems;
    }

    public List<ASTType> getAdded() {
        return unprocToItems;
    }

    public void add(double score, ASTType firstType, ASTType secondType) {
        matches.put(score, Pair.create(firstType, secondType));
    }

    public Collection<Pair<ASTType, ASTType>> get(double score) {
        return matches.get(score);
    }

    public List<ASTType> toAstTypeList(List<ItemType> its) {
        List<ASTType> astList = new ArrayList<ASTType>();
        for (ItemType it : its) {
            astList.add(items.getAstType(it));
        }
        return astList;
    }

    public List<Double> getDescendingScores() {
        List<Double> descendingScores = new ArrayList<Double>(new TreeSet<Double>(matches.keySet()));
        Collections.reverse(descendingScores);
        return descendingScores;
    }

    public void diff(List<ItemType> toItems, Differences differences) {
        List<ASTType> astTypes = toAstTypeList(toItems);
        getScores(astTypes);
        compareMatches(astTypes, differences);
    }

    private void getScores(List<ASTType> toTypes) {
        for (ASTType fromType : decls) {
            for (ASTType toType : toTypes) {
                double matchScore = fromType.getMatchScore(toType);
                if (matchScore > 0.0) {
                    add(matchScore, fromType, toType);
                }
            }
        }
    }

    private void compareMatches(List<ASTType> toItems, Differences differences) {
        unprocFromItems.clear();
        unprocToItems.clear();

        tr.Ace.red("decls", decls);

        unprocFromItems.addAll(decls);
        tr.Ace.red("unprocFromItems", unprocFromItems);

        unprocToItems.addAll(toItems);
        tr.Ace.red("unprocToItems", unprocToItems);

        List<Double> descendingScores = getDescendingScores();
        
        for (Double score : descendingScores) {
            tr.Ace.cyan("score", score);
            diffAtScore(score, differences);
        }
    }

    private void diffAtScore(double score, Differences differences) {
        // don't repeat comparisons ...

        List<ASTType> procFromItems = new ArrayList<ASTType>();
        List<ASTType> procToItems = new ArrayList<ASTType>();

        for (Pair<ASTType, ASTType> declPair : get(score)) {
            tr.Ace.magenta("declPair", declPair);
            ASTType fromType = declPair.getFirst();
            ASTType toType = declPair.getSecond();;

            tr.Ace.yellow("unprocFromItems", unprocFromItems);
            tr.Ace.yellow("fromType", fromType);
            tr.Ace.yellow("unprocFromItems.contains(fromType)", unprocFromItems.contains(fromType));

            if (unprocFromItems.contains(fromType) && unprocToItems.contains(toType)) {
                tr.Ace.bold("fromType", fromType);
                tr.Ace.bold("toType", toType);
                fromType.diff(toType, differences);
                    
                procFromItems.add(fromType);
                procToItems.add(toType);
            }
        }

        unprocFromItems.removeAll(procFromItems);
        unprocToItems.removeAll(procToItems);
    }
}
