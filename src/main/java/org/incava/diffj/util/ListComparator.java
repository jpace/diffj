package org.incava.diffj.util;

import java.util.ArrayList;
import java.util.List;

public class ListComparator<Type> {
    private final List<Type> from;
    private final List<Type> to;
    
    public ListComparator(List<Type> from, List<Type> to) {
        this.from = new ArrayList<Type>(from);
        this.to = new ArrayList<Type>(to);
    }

    public ListComparison getComparison() {
        ListComparison comp = new ListComparison();
        for (int idx = 0; idx < from.size(); ++idx) {
            Integer paramMatch = getListMatch(idx);
            if (paramMatch == null) {
                continue;
            }
            else if (paramMatch == idx) {
                comp.addExactMatch(idx);
            }
            else {
                comp.addMisorderedMatch(idx, paramMatch);
            }
        }
        return comp;
    }
    
    public void clearMatchList(int fromIndex, int toIndex) {
        from.set(fromIndex, null);
        to.set(toIndex, null);
    }

    public Integer getListMatch(int fromIndex) {
        int fromSize = from.size();
        Type fromVal = fromIndex < fromSize ? from.get(fromIndex) : null;
        
        if (fromVal == null) {
            return null;
        }

        int toSize = to.size();
        Type toVal = fromIndex < toSize ? to.get(fromIndex) : null;
        
        if (fromVal.equals(toVal)) {
            clearMatchList(fromIndex, fromIndex);
            return fromIndex;
        }
        
        for (int toIdx = 0; toIdx < toSize; ++toIdx) {
            toVal = to.get(toIdx);
            if (fromVal.equals(toVal)) {
                clearMatchList(fromIndex, toIdx);
                return toIdx;
            }
        }
        return null;
    }
 }
