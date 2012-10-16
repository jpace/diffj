package org.incava.diffj.element;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Messages;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.ItemUtil;

public class Access {
    public static final String ACCESS_REMOVED = "access removed: {0}";
    public static final String ACCESS_ADDED = "access added: {0}";
    public static final String ACCESS_CHANGED = "access changed from {0} to {1}";    

    private final SimpleNode node;

    public Access(SimpleNode node) {
        this.node = node;
    }

    public void diff(SimpleNode other, Differences differences) {
        Token fromAccess = ItemUtil.getAccess(node);
        Token toAccess = ItemUtil.getAccess(other);

        if (fromAccess == null) {
            if (toAccess != null) {
                differences.changed(node.getFirstToken(), toAccess, ACCESS_ADDED, toAccess.image);
            }
        }
        else if (toAccess == null) {
            differences.changed(fromAccess, other.getFirstToken(), ACCESS_REMOVED, fromAccess.image);
        }
        else if (!fromAccess.image.equals(toAccess.image)) {
            differences.changed(fromAccess, toAccess, ACCESS_CHANGED, fromAccess.image, toAccess.image);
        }
    }
}
