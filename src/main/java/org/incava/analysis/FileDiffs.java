package org.incava.analysis;

import java.util.TreeSet;

/**
 * A list/set/collection of FileDiffs, which knows that it was added to, even
 * after the set is cleared.
 */
public class FileDiffs extends TreeSet<FileDiff> {
    public static final long serialVersionUID = 1L;

    private boolean added;

    public FileDiffs() {
        added = false;
    }

    public boolean add(FileDiff fd) {
        added = true;
        return super.add(fd);
    }

    public boolean wasAdded() {
        return added;
    }
}
