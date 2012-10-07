package org.incava.diffj;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.*;

public class AccessDiff extends DiffComparator {    
    public AccessDiff(FileDiffs differences) {
        super(differences);
    }

    public void compareAccess(SimpleNode aNode, SimpleNode bNode) {
        Token aAccess = ItemUtil.getAccess(aNode);
        Token bAccess = ItemUtil.getAccess(bNode);

        if (aAccess == null) {
            if (bAccess != null) {
                changed(aNode.getFirstToken(), bAccess, Messages.ACCESS_ADDED, bAccess.image);
            }
        }
        else if (bAccess == null) {
            changed(aAccess, bNode.getFirstToken(), Messages.ACCESS_REMOVED, aAccess.image);
        }
        else if (!aAccess.image.equals(bAccess.image)) {
            changed(aAccess, bAccess, Messages.ACCESS_CHANGED, aAccess.image, bAccess.image);
        }
    }
}
