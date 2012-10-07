package org.incava.diffj;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.*;

public class AccessDiff extends DiffComparator {    
    public static final String ACCESS_REMOVED = "access removed: {0}";
    public static final String ACCESS_ADDED = "access added: {0}";
    public static final String ACCESS_CHANGED = "access changed from {0} to {1}";

    public AccessDiff(FileDiffs differences) {
        super(differences);
    }

    public void compareAccess(SimpleNode aNode, SimpleNode bNode) {
        Token aAccess = ItemUtil.getAccess(aNode);
        Token bAccess = ItemUtil.getAccess(bNode);

        if (aAccess == null) {
            if (bAccess != null) {
                changed(aNode.getFirstToken(), bAccess, ACCESS_ADDED, bAccess.image);
            }
        }
        else if (bAccess == null) {
            changed(aAccess, bNode.getFirstToken(), ACCESS_REMOVED, aAccess.image);
        }
        else if (!aAccess.image.equals(bAccess.image)) {
            changed(aAccess, bAccess, ACCESS_CHANGED, aAccess.image, bAccess.image);
        }
    }
}
