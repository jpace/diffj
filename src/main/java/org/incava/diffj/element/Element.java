package org.incava.diffj.element;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.Node;

public class Element {
    private final AbstractJavaNode node;
    
    public Element(AbstractJavaNode node) {
        this.node = node;
    }

    public AbstractJavaNode getNode() {
        return node;
    }

    public void dump() {
        Node.of(node).dump();
    }
}
