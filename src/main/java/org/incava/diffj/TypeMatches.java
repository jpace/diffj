package org.incava.diffj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;

public class TypeMatches<Type extends SimpleNode> {
    private final MultiMap<Double, Pair<Type, Type>> matches;

    public TypeMatches() {
        this.matches = new MultiMap<Double, Pair<Type, Type>>();
    }

    public void add(double score, Type firstType, Type secondType) {
        matches.put(score, Pair.create(firstType, secondType));
    }

    public Collection<Pair<Type, Type>> get(double score) {
        return matches.get(score);
    }

    public List<Double> getDescendingScores() {
        List<Double> descendingScores = new ArrayList<Double>(new TreeSet<Double>(matches.keySet()));
        Collections.reverse(descendingScores);
        return descendingScores;
    }
}
