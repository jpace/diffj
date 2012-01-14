package org.incava.diffj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.lang.Pair;
import org.incava.ijdk.util.MultiMap;

public class TypeMatches<Type extends SimpleNode> extends MultiMap<Double, Pair<Type, Type>> {
    public void add(double score, Type firstType, Type secondType) {
        put(score, Pair.create(firstType, secondType));
    }

    public List<Double> getDescendingScores() {
        List<Double> descendingScores = new ArrayList<Double>(new TreeSet<Double>(keySet()));
        Collections.reverse(descendingScores);
        return descendingScores;
    }
}
