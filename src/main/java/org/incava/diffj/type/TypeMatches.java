package org.incava.diffj.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.lang.NCPair;
import org.incava.ijdk.util.MultiMap;

public class TypeMatches<ElementType extends Diffable<ElementType>> {
    private final MultiMap<Double, NCPair<ElementType, ElementType>> matches;
    private final List<ElementType> unprocFromItems;
    private final List<ElementType> unprocToItems;
    private final List<ElementType> decls;

    public TypeMatches(List<ElementType> decls) {
        this.matches = new MultiMap<Double, NCPair<ElementType, ElementType>>();
        this.unprocFromItems = new ArrayList<ElementType>();
        this.unprocToItems = new ArrayList<ElementType>();
        this.decls = decls;
    }

    public List<ElementType> getRemoved() {
        return unprocFromItems;
    }

    public List<ElementType> getAdded() {
        return unprocToItems;
    }

    public void add(double score, ElementType firstType, ElementType secondType) {
        matches.add(score, NCPair.create(firstType, secondType));
    }

    public Collection<NCPair<ElementType, ElementType>> get(double score) {
        return matches.get(score);
    }

    public List<Double> getDescendingScores() {
        List<Double> descendingScores = new ArrayList<Double>(new TreeSet<Double>(matches.keySet()));
        Collections.reverse(descendingScores);
        return descendingScores;
    }

    public void diff(List<ElementType> toTypes, Differences differences) {
        addAllScores(toTypes);
        compareMatches(toTypes, differences);
    }

    private void addAllScores(List<ElementType> toTypes) {
        for (ElementType fromType : decls) {
            addScores(fromType, toTypes);
        }
    }

    private void addScores(ElementType fromType, List<ElementType> toTypes) {
        for (ElementType toType : toTypes) {
            double matchScore = fromType.getMatchScore(toType);
            if (matchScore > 0.0) {
                add(matchScore, fromType, toType);
            }
        }
    }
    
    private void compareMatches(List<ElementType> toItems, Differences differences) {
        unprocFromItems.addAll(decls);
        unprocToItems.addAll(toItems);

        List<Double> descendingScores = getDescendingScores();
        
        for (Double score : descendingScores) {
            diffAtScore(score, differences);
        }
    }

    private void diffAtScore(double score, Differences differences) {
        // don't repeat comparisons ...

        List<ElementType> procFromItems = new ArrayList<ElementType>();
        List<ElementType> procToItems = new ArrayList<ElementType>();

        for (NCPair<ElementType, ElementType> declPair : get(score)) {
            ElementType fromType = declPair.getFirst();
            ElementType toType = declPair.getSecond();

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
