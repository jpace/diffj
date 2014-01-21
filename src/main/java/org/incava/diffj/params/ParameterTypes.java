package org.incava.diffj.params;

import java.util.List;
import org.incava.ijdk.util.ListComparator;
import org.incava.ijdk.util.ListComparison;

public class ParameterTypes {
    private final List<String> paramTypes;
    
    public ParameterTypes(List<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public double getMatchScore(ParameterTypes toParmTyp) {
        return getMatchScore(toParmTyp.paramTypes);
    }

    public double getMatchScore(List<String> toParamTypes) {
        if (paramTypes.isEmpty() && toParamTypes.isEmpty()) {
            return 3;
        }
        
        // (int[], double, String) <=> (int[], double, String) ==> 100% (3 of 3)
        // (int[], double, String) <=> (double, int[], String) ==> 80% (3 of 3 - 10% * misordered)
        // (int[], double)         <=> (double, int[], String) ==> 46% (2 of 3 - 10% * misordered)
        // (int[], double, String) <=> (String) ==> 33% (1 of 3 params)
        // (int[], double) <=> (String) ==> 0 (0 of 3)

        ListComparator<String> lc = new ListComparator<String>(paramTypes, toParamTypes);

        ListComparison comp = lc.getComparison();

        // I used to diff both ways ((paramTypes, toParamTypes) and
        // (toParamTypes, paramTypes)), but that doesn't appear to be necessary.
        
        int numParams = Math.max(paramTypes.size(), toParamTypes.size());
        return 1 + (comp.getExactMatches().size() * 2 + comp.getMisorderedMatches().keySet().size()) / numParams;
    }
}
