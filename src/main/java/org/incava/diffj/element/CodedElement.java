package org.incava.diffj.element;

import net.sourceforge.pmd.ast.SimpleNode;

public abstract class CodedElement extends AccessibleElement {
    public CodedElement(SimpleNode node) {
        super(node);
    }

    abstract protected String getName();
}
