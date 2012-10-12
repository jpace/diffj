package org.incava.diffj.params;

public class ParameterMatch {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };

    private final Integer[] score;

    public ParameterMatch(Integer[] score) {
        this.score = score;
    }

    public Integer[] getScore() {
        return score;
    }
}
