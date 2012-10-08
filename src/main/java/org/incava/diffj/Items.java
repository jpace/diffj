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

    public void compareModifiers(SimpleNode aNode, SimpleNode bNode, int[] modifierTypes) {
        Modifiers mods = new Modifiers(differences.getFileDiffs());
        mods.compareModifiers(aNode, bNode, modifierTypes);
    }

    public void compareAccess(SimpleNode aNode, SimpleNode bNode) {
        Access acc = new Access(aNode);
        acc.diff(bNode, differences);
    }

    public void compareCode(String fromName, List<Token> fromList, List<Token> toList) {
        Code code = new Code(fromName, fromList);
        code.diff(toList, differences);
    }
}
