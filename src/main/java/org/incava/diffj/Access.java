package org.incava.diffj;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.*;

public class Access {
    private final DiffComparator differences;

    public Access(FileDiffs fileDiffs) {
        this.differences = new DiffComparator(fileDiffs);
    }

    public void compare(SimpleNode aNode, SimpleNode bNode) {
        Token aAccess = ItemUtil.getAccess(aNode);
        Token bAccess = ItemUtil.getAccess(bNode);

        if (aAccess == null) {
            if (bAccess != null) {
                differences.changed(aNode.getFirstToken(), bAccess, Messages.ACCESS_ADDED, bAccess.image);
            }
        }
        else if (bAccess == null) {
            differences.changed(aAccess, bNode.getFirstToken(), Messages.ACCESS_REMOVED, aAccess.image);
        }
        else if (!aAccess.image.equals(bAccess.image)) {
            differences.changed(aAccess, bAccess, Messages.ACCESS_CHANGED, aAccess.image, bAccess.image);
        }
    }
}
