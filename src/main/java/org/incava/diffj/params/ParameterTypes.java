package org.incava.diffj.params;

import java.util.List;
import org.incava.ijdk.lang.Pair;

public class ParameterTypes {
    private final List<String> paramTypes;
    
    public ParameterTypes(List<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

    protected boolean isEmpty() {
        return paramTypes.isEmpty();
    }

    private Pair<Integer, Integer> getMatchListScore(List<String> aParamTypes, List<String> bParamTypes, Pair<Integer, Integer> matches) {
        for (int idx = 0; idx < aParamTypes.size(); ++idx) {
            Integer paramMatch = getListMatch(aParamTypes, idx, bParamTypes);
            if (paramMatch == null) {
                continue;
            }
            else if (paramMatch == idx) {
                matches = Pair.create(matches.getFirst() + 1, matches.getSecond());
            }
            else {
                matches = Pair.create(matches.getFirst(), matches.getSecond() + 1);
            }
        }
        return matches;
    }

    public double getMatchScore(ParameterTypes toParmTyp) {
        if (isEmpty() && toParmTyp.isEmpty()) {
            return 1.0;
        }
        
        // (int[], double, String) <=> (int[], double, String) ==> 100% (3 of 3)
        // (int[], double, String) <=> (double, int[], String) ==> 80% (3 of 3 - 10% * misordered)
        // (int[], double)         <=> (double, int[], String) ==> 46% (2 of 3 - 10% * misordered)
        // (int[], double, String) <=> (String) ==> 33% (1 of 3 params)
        // (int[], double) <=> (String) ==> 0 (0 of 3)

        List<String> toParamTypes = toParmTyp.paramTypes;

        // first == number of exact matches; second == number of misordered matches:
        Pair<Integer, Integer> matches = getMatchListScore(paramTypes, toParamTypes, Pair.create(0, 0));
        matches = getMatchListScore(toParamTypes, paramTypes, matches);
        
        int numParams = Math.max(paramTypes.size(), toParamTypes.size());

        double matchCount = (double)matches.getFirst() / numParams + (double)matches.getSecond() / (2 * numParams);

        return 0.5 + (matchCount / 2.0);
    }

    private void clearMatchList(List<String> fromList, int fromIndex, List<String> toList, int toIndex) {
        fromList.set(fromIndex, null);
        toList.set(toIndex, null);
    }

    public Integer getListMatch(List<String> fromList, int fromIndex, List<String> toList) {
        int fromSize = fromList.size();
        String fromStr = fromIndex < fromSize ? fromList.get(fromIndex) : null;
        
        if (fromStr == null) {
            return null;
        }

        int toSize = toList.size();
        String toStr = fromIndex < toSize ? toList.get(fromIndex) : null;
        
        if (fromStr.equals(toStr)) {
            clearMatchList(fromList, fromIndex, toList, fromIndex);
            return fromIndex;
        }
        
        for (int toIdx = 0; toIdx < toSize; ++toIdx) {
            toStr = toList.get(toIdx);
            if (fromStr.equals(toStr)) {
                clearMatchList(fromList, fromIndex, toList, toIdx);
                return toIdx;
            }
        }
        return null;
    }
}
