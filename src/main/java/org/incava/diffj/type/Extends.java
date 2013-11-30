package org.incava.diffj.type;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.util.Messages;
import org.incava.ijdk.text.Message;

/**
 * Compares extends.
 */
public class Extends extends Supers {
    public static final Message EXTENDED_TYPE_REMOVED = new Message("extended type removed: {0}");
    public static final Message EXTENDED_TYPE_ADDED = new Message("extended type added: {0}");
    public static final Message EXTENDED_TYPE_CHANGED = new Message("extended type changed from {0} to {1}");
    public final static Messages EXTENDED_TYPE_MSGS = new Messages(EXTENDED_TYPE_ADDED, EXTENDED_TYPE_CHANGED, EXTENDED_TYPE_REMOVED);

    public Extends(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
    }

    protected Class<? extends SimpleNode> getPmdClass() {
        return net.sourceforge.pmd.ast.ASTExtendsList.class;
    }

    protected Message getAddedMessage() {
        return EXTENDED_TYPE_MSGS.getAdded();
    }

    protected Message getChangedMessage() {
        return EXTENDED_TYPE_MSGS.getChanged();
    }

    protected Message getRemovedMessage() {
        return EXTENDED_TYPE_MSGS.getDeleted();
    }
}
