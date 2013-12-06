package org.incava.diffj.params;

import java.util.ArrayList;
import java.util.List;

public class ParameterTypeComparator {
    private final List<String> fromParams;
    private final List<String> toParams;
    
    public ParameterTypeComparator(List<String> fromParams, List<String> toParams) {
        this.fromParams = new ArrayList<String>(fromParams);
        this.toParams = new ArrayList<String>(toParams);
    }

    public ParameterTypeComparison getComparison() {
        // first: exact; second: misordered
        ParameterTypeComparison comp = new ParameterTypeComparison(0, 0);
        for (int idx = 0; idx < fromParams.size(); ++idx) {
            Integer paramMatch = getListMatch(idx);
            if (paramMatch == null) {
                continue;
            }
            else if (paramMatch == idx) {
                comp = comp.addExactMatch();
            }
            else {
                comp = comp.addMisorderedMatch();
            }
        }
        return comp;
    }
    
    public void clearMatchList(int fromIndex, int toIndex) {
        fromParams.set(fromIndex, null);
        toParams.set(toIndex, null);
    }

    public Integer getListMatch(int fromIndex) {
        int fromSize = fromParams.size();
        String fromStr = fromIndex < fromSize ? fromParams.get(fromIndex) : null;
        
        if (fromStr == null) {
            return null;
        }

        int toSize = toParams.size();
        String toStr = fromIndex < toSize ? toParams.get(fromIndex) : null;
        
        if (fromStr.equals(toStr)) {
            clearMatchList(fromIndex, fromIndex);
            return fromIndex;
        }
        
        for (int toIdx = 0; toIdx < toSize; ++toIdx) {
            toStr = toParams.get(toIdx);
            if (fromStr.equals(toStr)) {
                clearMatchList(fromIndex, toIdx);
                return toIdx;
            }
        }
        return null;
    }
}
