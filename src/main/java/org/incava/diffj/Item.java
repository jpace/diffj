package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;

public class Item {
    public void compareAccess(SimpleNode fromNode, SimpleNode toNode, Differences differences) {
        Access acc = new Access(fromNode);
        acc.diff(toNode, differences);
    }

    public void compareCode(String fromName, List<Token> fromTokens, List<Token> toTokens, Differences differences) {
        // the from and to names are the same
        Code toCode = new Code(fromName, toTokens);
        Code fromCode = new Code(fromName, fromTokens);
        fromCode.diff(toCode, differences);
    }
}
