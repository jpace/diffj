package org.incava.diffj.type;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.ijdk.text.Message;

/**
 * Compares extends.
 */
public class Extends extends Supers {
    public static final String EXTENDED_TYPE_REMOVED = "extended type removed: {0}";
    public static final String EXTENDED_TYPE_ADDED = "extended type added: {0}";
    public static final String EXTENDED_TYPE_CHANGED = "extended type changed from {0} to {1}";

    public Extends(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
    }

    protected Class<? extends SimpleNode> getPmdClass() {
        return net.sourceforge.pmd.ast.ASTExtendsList.class;
    }

    protected String getAddedMessage() {
        return EXTENDED_TYPE_ADDED;
    }

    protected String getChangedMessage() {
        return EXTENDED_TYPE_CHANGED;
    }

    protected String getRemovedMessage() {
        return EXTENDED_TYPE_REMOVED;
    }
}
