package org.incava.diffj.type;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.Messages;

/**
 * Compares implements.
 */
public class Implements extends Supers {
    public Implements(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
    }

    protected Class<? extends SimpleNode> getPmdClass() {
        return net.sourceforge.pmd.ast.ASTImplementsList.class;
    }

    protected String getAddedMessage() {
        return Messages.IMPLEMENTED_TYPE_ADDED;
    }

    protected String getChangedMessage() {
        return Messages.IMPLEMENTED_TYPE_CHANGED;
    }

    protected String getRemovedMessage() {
        return Messages.IMPLEMENTED_TYPE_REMOVED;
    }
}
