package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.params.Parameters;
import org.incava.pmdx.MethodUtil;
import org.incava.pmdx.ParameterUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Method extends Function implements Diffable<Method> {
    private final ASTMethodDeclaration method;
    private final ASTBlock block;

    public Method(ASTMethodDeclaration method) {
        super(method);
        this.method = method;
        this.block = (ASTBlock)SimpleNodeUtil.findChild(method, "net.sourceforge.pmd.ast.ASTBlock");
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

    protected ASTNameList getThrowsList() {
        return MethodUtil.getThrowsList(method);
    }

    protected ASTBlock getBlock() {
        return block;
    }

    protected SimpleNode getReturnType() {
        return SimpleNodeUtil.findChild(method);
    }

    /**
     * This returns the full method name/signature, including parameters.
     */
    public String getName() {
        return MethodUtil.getFullName(method);
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
        if (hasBlock()) {
            if (toMethod.hasBlock()) {
                compareCode(toMethod, differences);
            }
            else {
                differences.changed(method, toMethod.method, Messages.METHOD_BLOCK_REMOVED);
            }
        }
        else if (toMethod.hasBlock()) {
            differences.changed(method, toMethod.method, Messages.METHOD_BLOCK_ADDED);
        }
    }

    // protected void compareBlocks(String fromName, ASTBlock fromBlock, String toName, ASTBlock toBlock)
    // {
    //     tr.Ace.cyan("fromBlock", fromBlock);
    //     SimpleNodeUtil.dump(fromBlock, "");
    //     tr.Ace.cyan("toBlock", toBlock);
    //     SimpleNodeUtil.dump(toBlock, "");
    //     // walk through, looking for common if and for statements ...
    //     tr.Ace.cyan("aChildren(null)", SimpleNodeUtil.findChildren(fromBlock));
    //     tr.Ace.cyan("bChildren(null)", SimpleNodeUtil.findChildren(toBlock));        
    // }

    protected List<Token> getCodeTokens() {
        return SimpleNodeUtil.getChildTokens(block);
    }

    protected void compareReturnTypes(Method toMethod, Differences differences) {
        SimpleNode fromRetType    = getReturnType();
        SimpleNode toRetType      = toMethod.getReturnType();
        String     fromRetTypeStr = SimpleNodeUtil.toString(fromRetType);
        String     toRetTypeStr   = SimpleNodeUtil.toString(toRetType);

        if (!fromRetTypeStr.equals(toRetTypeStr)) {
            differences.changed(fromRetType, toRetType, Messages.RETURN_TYPE_CHANGED, fromRetTypeStr, toRetTypeStr);
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

    public String getAddedMessage() {
        return Messages.METHOD_ADDED;
    }

    public String getRemovedMessage() {
        return Messages.METHOD_REMOVED;
    }
}
