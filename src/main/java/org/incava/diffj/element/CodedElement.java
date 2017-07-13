package org.incava.diffj.element;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;

public abstract class CodedElement extends AccessibleElement {
    public CodedElement(AbstractJavaNode node) {
        super(node);
    }

    abstract protected String getName();
}
