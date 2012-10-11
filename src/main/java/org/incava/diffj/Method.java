package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.MethodUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Method extends Function {
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

    protected ASTFormalParameters getParameters() {
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

    protected String getName() {
        return MethodUtil.getFullName(method);
    }

    protected void compareModifiers(Method toMethod, Differences differences) {
        SimpleNode fromParent = getParent();
        SimpleNode toParent = toMethod.getParent();
        MethodModifiers fromMods = new MethodModifiers(fromParent);
        MethodModifiers toMods = new MethodModifiers(toParent);
        fromMods.diff(toMods, differences);
    }

    protected void compareParameters(Method toMethod, Differences differences) {
        ASTFormalParameters fromFormalParams = getParameters();
        ASTFormalParameters toFormalParams = toMethod.getParameters();
        compareParameters(fromFormalParams, toFormalParams, differences);
    }

    protected boolean hasBlock() {
        return block != null;
    }

    protected void compareBodies(Method toMethod, Differences differences) {
        // tr.Ace.log("method", method);
        // tr.Ace.log("toMethod", toMethod);

        if (hasBlock()) {
            if (toMethod.hasBlock()) {
                compareBlocks(toMethod, differences);
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

    protected void compareBlocks(Method toMethod, Differences differences) {
        String fromName = getName();
        List<Token> fromTokens = getCodeTokens();
        List<Token> toTokens = toMethod.getCodeTokens();
        compareCode(fromName, fromTokens, toTokens, differences);
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
}
