package org.incava.diffj.element;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import org.incava.pmdx.Node;

public class AccessibleElement extends Element {
    public AccessibleElement(AbstractJavaNode node) {
        super(node);
    }

    public void compareAccess(AccessibleElement toElement, Differences differences) {
        Access acc = new Access(getParent());
        acc.diff(toElement.getParent(), differences);
    }

    public AbstractJavaNode getParent() {
        Node<AbstractJavaNode> node = Node.of(getNode());
        return node.getParent();
    }
}
