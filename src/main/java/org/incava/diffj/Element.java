package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.SimpleNodeUtil;

public class Element {
    private final SimpleNode node;
    
    public Element(SimpleNode node) {
        this.node = node;
    }

    public SimpleNode getNode() {
        return node;
    }

    public void compareAccess(Element toElement, Differences differences) {
        Access acc = new Access(getParent());
        acc.diff(toElement.getParent(), differences);
    }

    public SimpleNode getParent() {
        return SimpleNodeUtil.getParent(node);
    }
}
