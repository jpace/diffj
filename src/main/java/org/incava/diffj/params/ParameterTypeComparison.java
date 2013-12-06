package org.incava.diffj.params;

public class ParameterTypeComparison {
    private final int exactMatches;
    private final int misorderedMatches;
    
    public ParameterTypeComparison(int exactMatches, int misorderedMatches) {
        this.exactMatches = exactMatches;
        this.misorderedMatches = misorderedMatches;
    }
    
    public ParameterTypeComparison addExactMatch() {
        return new ParameterTypeComparison(exactMatches + 1, misorderedMatches);
    }

    public ParameterTypeComparison addMisorderedMatch() {
        return new ParameterTypeComparison(exactMatches, misorderedMatches + 1);
    }

    public int getExactMatches() {
        return exactMatches;
    }

    public int getMisorderedMatches() {
        return misorderedMatches;
    }
}
