package org.incava.diffj;

/**
 * Something that can be compared against another.
 */
public interface Diffable<DiffType extends Diffable> {
    public double getMatchScore(DiffType toDiffable);

    public void diff(DiffType toDiffable, Differences differences);
}
