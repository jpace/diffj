package org.incava.diffj.element;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.ItemUtil;
import org.incava.pmdx.Node;

public class Access {
    public static final Message ACCESS_REMOVED = new Message("access removed: {0}");
    public static final Message ACCESS_ADDED = new Message("access added: {0}");
    public static final Message ACCESS_CHANGED = new Message("access changed from {0} to {1}");

    private final AbstractJavaNode node;

    public Access(AbstractJavaNode node) {
        this.node = node;
    }

    public void diff(AbstractJavaNode other, Differences differences) {
        Token fromAccess = ItemUtil.getAccess(node);
        Token toAccess = ItemUtil.getAccess(other);

        if (fromAccess == null) {
            if (toAccess != null) {
                differences.changed(Node.of(node).getFirstToken(), toAccess, ACCESS_ADDED, toAccess.image);
            }
        }
        else if (toAccess == null) {
            differences.changed(fromAccess, Node.of(other).getFirstToken(), ACCESS_REMOVED, fromAccess.image);
        }
        else if (!fromAccess.image.equals(toAccess.image)) {
            differences.changed(fromAccess, toAccess, ACCESS_CHANGED, fromAccess.image, toAccess.image);
        }
    }
}
