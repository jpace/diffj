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
    
    public Item() {
        this(null);
    }

    public void compareAccess(SimpleNode toNode, Differences differences) {
        Access acc = new Access(getParent());
        acc.diff(toNode, differences);
    }

    public void compareAccess(Item toItem, Differences differences) {
        Access acc = new Access(getParent());
        acc.diff(toItem.getParent(), differences);
    }

    public void compareCode(String fromName, List<Token> fromTokens, List<Token> toTokens, Differences differences) {
        // the from and to names are the same
        Code toCode = new Code(fromName, toTokens);
        Code fromCode = new Code(fromName, fromTokens);
        fromCode.diff(toCode, differences);
    }

    public SimpleNode getParent() {
        return SimpleNodeUtil.getParent(node);
    }
}
