package org.incava.diffj.type;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.Messages;

/**
 * Compares implements.
 */
public class Implements extends Supers {
    public static final String IMPLEMENTED_TYPE_REMOVED = "implemented type removed: {0}";
    public static final String IMPLEMENTED_TYPE_ADDED = "implemented type added: {0}";
    public static final String IMPLEMENTED_TYPE_CHANGED = "implemented type changed from {0} to {1}";

    public Implements(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
    }

    protected Class<? extends SimpleNode> getPmdClass() {
        return net.sourceforge.pmd.ast.ASTImplementsList.class;
    }

    protected String getAddedMessage() {
        return IMPLEMENTED_TYPE_ADDED;
    }

    protected String getChangedMessage() {
        return IMPLEMENTED_TYPE_CHANGED;
    }

    protected String getRemovedMessage() {
        return IMPLEMENTED_TYPE_REMOVED;
    }
}
