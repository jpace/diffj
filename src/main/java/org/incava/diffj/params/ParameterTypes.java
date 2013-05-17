package org.incava.diffj.params;

import java.util.List;

public class ParameterTypes {
    private final List<String> paramTypes;
    
    public ParameterTypes(List<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

    protected boolean isEmpty() {
        return paramTypes.isEmpty();
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

        int fromSize = paramTypes.size();
        int toSize = toParamTypes.size();

        int exactMatches = 0;
        int misorderedMatches = 0;
            
        for (int fromIdx = 0; fromIdx < fromSize; ++fromIdx) {
            int paramMatch = getListMatch(paramTypes, fromIdx, toParamTypes);
            if (paramMatch == fromIdx) {
                ++exactMatches;
            }
            else if (paramMatch >= 0) {
                ++misorderedMatches;
            }
        }

        for (int toIdx = 0; toIdx < toSize; ++toIdx) {
            int paramMatch = getListMatch(toParamTypes, toIdx, paramTypes);
            if (paramMatch == toIdx) {
                ++exactMatches;
            }
            else if (paramMatch >= 0) {
                ++misorderedMatches;
            }
        }

        int numParams = Math.max(fromSize, toSize);
        double match = (double)exactMatches / numParams + (double)misorderedMatches / (2 * numParams);

        return 0.5 + (match / 2.0);
    }

    private void clearMatchList(List<String> fromList, int fromIndex, List<String> toList, int toIndex) {
        fromList.set(fromIndex, null);
        toList.set(toIndex, null);
    }

    public int getListMatch(List<String> fromList, int fromIndex, List<String> toList) {
        int fromSize = fromList.size();
        String fromStr = fromIndex < fromSize ? fromList.get(fromIndex) : null;
        
        if (fromStr == null) {
            return -1;
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
        return -1;
    }
}
