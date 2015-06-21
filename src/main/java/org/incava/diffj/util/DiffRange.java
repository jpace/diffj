package org.incava.diffj.util;

import java.util.*;

/**
 * Two diff points, delete and add.
 */
public class DiffRange {
    private final DiffPoint deleted;
    private final DiffPoint added;

    public DiffRange(DiffPoint deleted, DiffPoint added) {
        this.deleted = deleted;
        this.added = added;
    }

    public DiffPoint getDeletedPoint() {
        return deleted;
    }

    public DiffPoint getAddedPoint() {
        return added;
    }

    public Integer getDeletedStart() {
        return deleted.getStart();
    }

    public Integer getDeletedEnd() {
        return deleted.getEnd();
    }

    public Integer getAddedStart() {
        return added.getStart();
    }

    public Integer getAddedEnd() {
        return added.getEnd();
    }
}
