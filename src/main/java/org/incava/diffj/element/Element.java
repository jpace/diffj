package org.incava.diffj.element;

import net.sourceforge.pmd.ast.SimpleNode;

public class Element {
    private final SimpleNode node;
    
    public Element(SimpleNode node) {
        this.node = node;
    }

    public SimpleNode getNode() {
        return node;
    }
}
