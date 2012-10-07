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
        ModifiersDiff md = new ModifiersDiff(getFileDiffs());
        md.compareModifiers(aNode, bNode, modifierTypes);
    }

    public void compareAccess(SimpleNode aNode, SimpleNode bNode) {
        AccessDiff ad = new AccessDiff(getFileDiffs());
        ad.compare(aNode, bNode);
    }

    public void compareCode(String fromName, List<Token> fromList, String toName, List<Token> toList) {
        CodeDiff cd = new CodeDiff(getFileDiffs());
        cd.compareCode(fromName, fromList, toName, toList);
    }
}
