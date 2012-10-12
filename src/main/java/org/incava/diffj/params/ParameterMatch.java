package org.incava.diffj.params;

public class ParameterMatch {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };

    private final Integer[] score;

    public ParameterMatch(Integer[] score) {
        this.score = score;
    }

    public ParameterMatch(int typeMatch, int nameMatch) {
        this.score = new Integer[] { typeMatch, nameMatch };
    }

    public Integer[] getScore() {
        return score;
    }

    public int getTypeMatch() {
        return score[0];
    }

    public int getNameMatch() {
        return score[1];
    }
    
    public boolean isExactMatch() {
        return getTypeMatch() == getNameMatch();
    }
}
