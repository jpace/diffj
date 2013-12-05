package org.incava.diffj.function;

import java.util.List;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import org.incava.diffj.code.Block;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;
import org.incava.diffj.params.Parameters;
import org.incava.diffj.util.Messages;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.CtorUtil;

public class Ctor extends Function implements Diffable<Ctor> {
    public final static Message CONSTRUCTOR_REMOVED = new Message("constructor removed: {0}");
    public final static Message CONSTRUCTOR_ADDED = new Message("constructor added: {0}");
    public final static Messages CTOR_MESSAGES = new Messages(CONSTRUCTOR_ADDED, null, CONSTRUCTOR_REMOVED);

    private final ASTConstructorDeclaration ctor;

    public Ctor(ASTConstructorDeclaration ctor) {
        super(ctor);
        this.ctor = ctor;
    }

    public void diff(Ctor toCtor, Differences differences) {
        compareAccess(toCtor, differences);
        compareParameters(toCtor, differences);
        compareThrows(toCtor, differences);
        compareCode(toCtor, differences);
    }

    protected ASTFormalParameters getFormalParameters() {
        return CtorUtil.getParameters(ctor);
    }

    public String getName() {
        return CtorUtil.getFullName(ctor);
    }    

    public void compareCode(Ctor toCtor, Differences differences) {
        Block fromBlock = new Block(getName(), ctor);
        Block toBlock = new Block(toCtor.getName(), toCtor.ctor);
        fromBlock.compareCode(toBlock, differences);
    }

    public double getMatchScore(Ctor toCtor) {
        Parameters fromParams = getParameters();
        Parameters toParams = toCtor.getParameters();
        return fromParams.getMatchScore(toParams);
    }

    public Message getAddedMessage() {
        return CTOR_MESSAGES.getAdded();
    }

    public Message getRemovedMessage() {
        return CTOR_MESSAGES.getDeleted();
    }
}
