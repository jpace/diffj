package org.incava.diffj.element;

import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.pmdx.SimpleNodeUtil;

public class AccessibleElement extends Element {
    public AccessibleElement(SimpleNode node) {
        super(node);
    }

    public void compareAccess(AccessibleElement toElement, Differences differences) {
        Access acc = new Access(getParent());
        acc.diff(toElement.getParent(), differences);
    }

    public SimpleNode getParent() {
        SimpleNode node = getNode();
        return SimpleNodeUtil.getParent(node);
    }
}
