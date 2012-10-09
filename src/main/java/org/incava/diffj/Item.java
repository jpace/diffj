package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;

public class Item {
    protected final Differences differences;
    
    public Item(FileDiffs fileDiffs) {
        this.differences = new Differences(fileDiffs);
    }
    
    public Item(Differences differences) {
        this.differences = differences;
    }

    public Item() {
        this.differences = null;
    }

    public void compareAccess(SimpleNode fromNode, SimpleNode toNode, Differences differences) {
        Access acc = new Access(fromNode);
        acc.diff(toNode, differences);
    }

    public void compareCode(String fromName, List<Token> fromTokens, List<Token> toList, Differences differences) {
        Code code = new Code(fromName, fromTokens);
        code.diff(toList, differences);
    }
}
