package org.incava.diffj.function;

import net.sourceforge.pmd.ast.ASTInitializer;
import org.incava.diffj.element.CodedElement;
import org.incava.diffj.util.Messages;
import org.incava.ijdk.text.Message;

public class Initializer implements Diffable<Initializer> {
    // public static final Message METHOD_REMOVED = new Message("method removed: {0}");
    // public static final Message METHOD_ADDED = new Message("method added: {0}");
    // public final static Messages METHOD_MSGS = new Messages(METHOD_ADDED, null, METHOD_REMOVED);

    // public static final Message RETURN_TYPE_CHANGED = new Message("return type changed from {0} to {1}");
    // public static final Message METHOD_BLOCK_ADDED = new Message("method block added");
    // public static final Message METHOD_BLOCK_REMOVED = new Message("method block removed");

    private final ASTInitializer init;
    // private final Block block;
    // private final String name;

    public Initializer(ASTInitializer init) {
        this.init = init;
    }

    public double getMatchScore(DiffType toDiffable) {
        return -1;
    }

    public void diff(DiffType toDiffable, Differences differences) {
    }

    public String getName() {
        return null;
    }

    public Message getAddedMessage() {
        return null;
    }

    public Message getRemovedMessage() {
        return null;
    }

    public SimpleNode getNode() {
        return null;
    }
}
