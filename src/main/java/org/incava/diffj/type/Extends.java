package org.incava.diffj.type;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.text.Message;

/**
 * Compares extends.
 */
public class Extends extends Supers {
    public static final Message EXTENDED_TYPE_REMOVED = new Message("extended type removed: {0}");
    public static final Message EXTENDED_TYPE_ADDED = new Message("extended type added: {0}");
    public static final Message EXTENDED_TYPE_CHANGED = new Message("extended type changed from {0} to {1}");

    public Extends(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
    }

    protected Class<? extends SimpleNode> getPmdClass() {
        return net.sourceforge.pmd.ast.ASTExtendsList.class;
    }

    protected Message getAddedMessage() {
        return EXTENDED_TYPE_ADDED;
    }

    protected Message getChangedMessage() {
        return EXTENDED_TYPE_CHANGED;
    }

    protected Message getRemovedMessage() {
        return EXTENDED_TYPE_REMOVED;
    }
}
