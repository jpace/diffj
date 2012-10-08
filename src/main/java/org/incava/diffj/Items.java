package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;

public class Items {
    protected final Differences differences;
    
    public Items(FileDiffs fileDiffs) {
        this.differences = new Differences(fileDiffs);
    }
    
    public Items(Differences differences) {
        this.differences = differences;
    }

    public void compareModifiers(SimpleNode fromNode, SimpleNode toNode, int[] modifierTypes) {
        Modifiers mods = new Modifiers(fromNode);
        mods.diff(toNode, modifierTypes, differences);
    }

    public void compareAccess(SimpleNode fromNode, SimpleNode toNode) {
        Access acc = new Access(fromNode);
        acc.diff(toNode, differences);
    }

    public void compareCode(String fromName, List<Token> fromTokens, List<Token> toList) {
        Code code = new Code(fromName, fromTokens);
        code.diff(toList, differences);
    }
}
