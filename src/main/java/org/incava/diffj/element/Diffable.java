package org.incava.diffj.element;

import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.text.Message;

/**
 * Something that can be compared against another.
 */
public interface Diffable<DiffType extends Diffable<DiffType>> {
    public double getMatchScore(DiffType toDiffable);

    public void diff(DiffType toDiffable, Differences differences);

    public String getName();

    public Message getAddedMessage();

    public Message getRemovedMessage();

    public SimpleNode getNode();
}
