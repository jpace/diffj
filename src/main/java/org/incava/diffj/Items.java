package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;

public class Items {
    protected final DiffComparator differences;
    
    public Items(FileDiffs fileDiffs) {
        this.differences = new DiffComparator(fileDiffs);
    }

    public void compareModifiers(SimpleNode aNode, SimpleNode bNode, int[] modifierTypes) {
        Modifiers mods = new Modifiers(differences.getFileDiffs());
        mods.compareModifiers(aNode, bNode, modifierTypes);
    }

    public void compareAccess(SimpleNode aNode, SimpleNode bNode) {
        Access acc = new Access(differences.getFileDiffs());
        acc.compare(aNode, bNode);
    }

    public void compareCode(String fromName, List<Token> fromList, String toName, List<Token> toList) {
        Code code = new Code(differences.getFileDiffs());
        code.compareCode(fromName, fromList, toName, toList);
    }
}
