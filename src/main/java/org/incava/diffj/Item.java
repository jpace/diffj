package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.SimpleNodeUtil;

public class Item {
    private final SimpleNode node;
    
    public Item(SimpleNode node) {
        this.node = node;
    }

    public SimpleNode getNode() {
        return node;
    }

    public void compareAccess(Item toItem, Differences differences) {
        Access acc = new Access(getParent());
        acc.diff(toItem.getParent(), differences);
    }

    public SimpleNode getParent() {
        return SimpleNodeUtil.getParent(node);
    }
}
