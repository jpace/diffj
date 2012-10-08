package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.analysis.Report;
import org.incava.pmdx.*;

public class ItemDiff extends DiffComparator {    
    public ItemDiff(Report report) {
        super(report);
    }

    public ItemDiff(FileDiffs differences) {
        super(differences);
    }

    public void compareModifiers(SimpleNode aNode, SimpleNode bNode, int[] modifierTypes) {
        Modifiers mods = new Modifiers(getFileDiffs());
        mods.compareModifiers(aNode, bNode, modifierTypes);
    }

    public void compareAccess(SimpleNode aNode, SimpleNode bNode) {
        Access acc = new Access(getFileDiffs());
        acc.compare(aNode, bNode);
    }

    public void compareCode(String fromName, List<Token> fromList, String toName, List<Token> toList) {
        Code code = new Code(getFileDiffs());
        code.compareCode(fromName, fromList, toName, toList);
    }
}
