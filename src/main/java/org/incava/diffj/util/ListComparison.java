package org.incava.diffj.util;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ListComparison {
    private final List<Integer> exactMatches;
    private final Map<Integer, Integer> misorderedMatches;
    
    public ListComparison() {
        exactMatches = new ArrayList<Integer>();
        misorderedMatches = new HashMap<Integer, Integer>();
    }
    
    public void addExactMatch(Integer idx) {
        exactMatches.add(idx);
    }

    public void addMisorderedMatch(Integer fromIdx, Integer toIdx) {
        misorderedMatches.put(fromIdx, toIdx);
    }

    public List<Integer> getExactMatches() {
        return exactMatches;
    }

    public Map<Integer, Integer> getMisorderedMatches() {
        return misorderedMatches;
    }
}
