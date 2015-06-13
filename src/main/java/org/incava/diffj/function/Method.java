package org.incava.diffj.function;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.code.Block;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;
import org.incava.diffj.params.Parameters;
import org.incava.diffj.util.Messages;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.MethodUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Method extends Function implements Diffable<Method> {
    public static final Message METHOD_REMOVED = new Message("method removed: {0}");
    public static final Message METHOD_ADDED = new Message("method added: {0}");
    public final static Messages METHOD_MSGS = new Messages(METHOD_ADDED, null, METHOD_REMOVED);

    public static final Message RETURN_TYPE_CHANGED = new Message("return type changed from {0} to {1}");
    public static final Message METHOD_BLOCK_ADDED = new Message("method block added");
    public static final Message METHOD_BLOCK_REMOVED = new Message("method block removed");

    private final ASTMethodDeclaration method;
    private final Block block;
    private final String name;

    public Method(ASTMethodDeclaration method) {
        super(method);
        this.method = method;
        ASTBlock astBlk = SimpleNodeUtil.findChild(method, ASTBlock.class);
        this.name = MethodUtil.getFullName(method);
        this.block = astBlk == null ? null : new Block(name, astBlk);
    }

    public void diff(Method toMethod, Differences differences) {
        compareAccess(toMethod, differences);
        compareModifiers(toMethod, differences);
        compareReturnTypes(toMethod, differences);
        compareParameters(toMethod, differences);
        compareThrows(toMethod, differences);
        compareBodies(toMethod, differences);
    }

    protected ASTFormalParameters getFormalParameters() {
        return MethodUtil.getParameters(method);
    }

    protected Block getBlock() {
        return block;
    }

    protected SimpleNode getReturnType() {
        return SimpleNodeUtil.findChild(method);
    }

    /**
     * This returns the full method name/signature, including parameters.
     */
    public String getName() {
        return name;
    }

    protected MethodModifiers getModifiers() {
        return new MethodModifiers(getParent());
    }

    protected void compareModifiers(Method toMethod, Differences differences) {
        MethodModifiers fromMods = getModifiers();
        MethodModifiers toMods = toMethod.getModifiers();
        fromMods.diff(toMods, differences);
    }

    protected boolean hasBlock() {
        return block != null;
    }

    protected void compareBodies(Method toMethod, Differences differences) {
        tr.Ace.log("name", getName());

        if (hasBlock()) {
            if (toMethod.hasBlock()) {
                block.compareCode(toMethod.block, differences);
            }
            else {
                differences.changed(this, toMethod, METHOD_BLOCK_REMOVED);
            }
        }
        else if (toMethod.hasBlock()) {
            differences.changed(this, toMethod, METHOD_BLOCK_ADDED);
        }
    }

    protected void compareReturnTypes(Method toMethod, Differences differences) {
        SimpleNode fromRetType    = getReturnType();
        SimpleNode toRetType      = toMethod.getReturnType();
        String     fromRetTypeStr = SimpleNodeUtil.toString(fromRetType);
        String     toRetTypeStr   = SimpleNodeUtil.toString(toRetType);

        if (!fromRetTypeStr.equals(toRetTypeStr)) {
            differences.changed(fromRetType, toRetType, RETURN_TYPE_CHANGED, fromRetTypeStr, toRetTypeStr);
        }
    }

    /**
     * This returns only the method name, without the parameters.
     */
    public String getMethodName() {
        ASTMethodDeclarator decl = MethodUtil.getDeclarator(method);
        return decl.getFirstToken().image;
    }

    public double getMatchScore(Method toMethod) {
        String fromName = getMethodName();
        String toName = toMethod.getMethodName();

        if (!fromName.equals(toName)) {
            return 0;
        }

        Parameters fromParams = getParameters();
        Parameters toParams = toMethod.getParameters();

        return fromParams.getMatchScore(toParams);
    }

    public Message getAddedMessage() {
        return METHOD_MSGS.getAdded();
    }

    public Message getRemovedMessage() {
        return METHOD_MSGS.getDeleted();
    }
}
