package org.incava.diffj.function;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.code.Block;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;
import org.incava.diffj.util.Messages;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.SimpleNodeUtil;

public class Initializer implements Diffable<Initializer> {
    public static final Message STATIC_BLOCK_REMOVED = new Message("static block removed");
    public static final Message STATIC_BLOCK_ADDED = new Message("static block added");
    public final static Messages STATIC_BLOCK_MSGS = new Messages(STATIC_BLOCK_ADDED, null, STATIC_BLOCK_REMOVED);

    private final ASTInitializer init;
    private final Block block;

    public Initializer(ASTInitializer init) {
        this.init = init;
        ASTBlock astBlk = SimpleNodeUtil.findChild(init, ASTBlock.class);
        this.block = astBlk == null ? null : new Block("static block", astBlk);
    }

    public double getMatchScore(Initializer toDiffable) {
        return 1.0;
    }

    public void diff(Initializer toInit, Differences differences) {
        block.compareCode(toInit.block, differences);
    }

    public String getName() {
        return null;
    }

    public Message getAddedMessage() {
        return STATIC_BLOCK_MSGS.getAdded();
    }

    public Message getRemovedMessage() {
        return STATIC_BLOCK_MSGS.getDeleted();
    }

    public SimpleNode getNode() {
        return init;
    }
}
