package org.incava.diffj;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.*;

public class Access {
    private final SimpleNode node;

    public Access(SimpleNode node) {
        this.node = node;
    }

    public void diff(SimpleNode other, Differences differences) {
        Token fromAccess = ItemUtil.getAccess(node);
        Token toAccess = ItemUtil.getAccess(other);

        if (fromAccess == null) {
            if (toAccess != null) {
                differences.changed(node.getFirstToken(), toAccess, Messages.ACCESS_ADDED, toAccess.image);
            }
        }
        else if (toAccess == null) {
            differences.changed(fromAccess, other.getFirstToken(), Messages.ACCESS_REMOVED, fromAccess.image);
        }
        else if (!fromAccess.image.equals(toAccess.image)) {
            differences.changed(fromAccess, toAccess, Messages.ACCESS_CHANGED, fromAccess.image, toAccess.image);
        }
    }
}
