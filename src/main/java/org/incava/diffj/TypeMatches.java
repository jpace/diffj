package org.incava.diffj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;

public class TypeMatches<ASTType extends Diffable<ASTType>> {
    private final MultiMap<Double, Pair<ASTType, ASTType>> matches;
    private final List<ASTType> unprocFromItems;
    private final List<ASTType> unprocToItems;
    private final List<ASTType> decls;

    public TypeMatches(List<ASTType> decls) {
        this.matches = new MultiMap<Double, Pair<ASTType, ASTType>>();
        this.unprocFromItems = new ArrayList<ASTType>();
        this.unprocToItems = new ArrayList<ASTType>();
        this.decls = decls;
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

    public List<Double> getDescendingScores() {
        List<Double> descendingScores = new ArrayList<Double>(new TreeSet<Double>(matches.keySet()));
        Collections.reverse(descendingScores);
        return descendingScores;
    }

    public void diff(List<ASTType> toTypes, Differences differences) {
        addAllScores(toTypes);
        compareMatches(toTypes, differences);
    }

    private void addAllScores(List<ASTType> toTypes) {
        for (ASTType fromType : decls) {
            addScores(fromType, toTypes);
        }
    }

    private void addScores(ASTType fromType, List<ASTType> toTypes) {
        for (ASTType toType : toTypes) {
            double matchScore = fromType.getMatchScore(toType);
            if (matchScore > 0.0) {
                add(matchScore, fromType, toType);
            }
        }
    }
    
    private void compareMatches(List<ASTType> toItems, Differences differences) {
        unprocFromItems.addAll(decls);
        unprocToItems.addAll(toItems);

        List<Double> descendingScores = getDescendingScores();
        
        for (Double score : descendingScores) {
            diffAtScore(score, differences);
        }
    }

    private void diffAtScore(double score, Differences differences) {
        // don't repeat comparisons ...

        List<ASTType> procFromItems = new ArrayList<ASTType>();
        List<ASTType> procToItems = new ArrayList<ASTType>();

        for (Pair<ASTType, ASTType> declPair : get(score)) {
            ASTType fromType = declPair.getFirst();
            ASTType toType = declPair.getSecond();;

            if (unprocFromItems.contains(fromType) && unprocToItems.contains(toType)) {
                fromType.diff(toType, differences);
                    
                procFromItems.add(fromType);
                procToItems.add(toType);
            }
        }

        unprocFromItems.removeAll(procFromItems);
        unprocToItems.removeAll(procToItems);
    }
}
