package org.incava.diffj.params;

public class ParameterMatch {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };

    private final int typeMatch;
    private final int nameMatch;

    public ParameterMatch(int typeMatch, int nameMatch) {
        this.typeMatch = typeMatch;
        this.nameMatch = nameMatch;
    }

    public int getTypeMatch() {
        return typeMatch;
    }

    public int getNameMatch() {
        return nameMatch;
    }
    
    public boolean isMatch(int idx) {
        return typeMatch == idx && nameMatch == idx;
    }

    public boolean isExactMatch() {
        int typeMatch = getTypeMatch();
        return typeMatch >= 0 && getTypeMatch() == getNameMatch();
    }

    public int getFirstMatch() {
        int typeMatch = getTypeMatch();
        return typeMatch >= 0 ? typeMatch : getNameMatch();
    }

    public String toString() {
        return String.valueOf(typeMatch) + ", " + nameMatch;
    }
}
