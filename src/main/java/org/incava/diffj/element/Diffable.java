package org.incava.diffj.element;

import net.sourceforge.pmd.ast.SimpleNode;

/**
 * Something that can be compared against another.
 */
public interface Diffable<DiffType extends Diffable> {
    public double getMatchScore(DiffType toDiffable);

    public void diff(DiffType toDiffable, Differences differences);

    public String getName();

    public String getAddedMessage();

    public String getRemovedMessage();

    public SimpleNode getNode();
}
