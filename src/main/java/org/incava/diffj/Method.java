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

    public Method(ASTMethodDeclaration method) {
        this.method = method;
    }

    public void diff(Method toMethod, Differences differences) {
        compareAccess(getParent(), toMethod.getParent(), differences);
        compareModifiers(toMethod, differences);
        compareReturnTypes(toMethod, differences);
        compareParameters(toMethod, differences);
        compareThrows(toMethod, differences);
        compareBodies(toMethod, differences);
    }

    protected SimpleNode getParent() {
        return SimpleNodeUtil.getParent(method);
    }

    protected ASTFormalParameters getParameters() {
        return MethodUtil.getParameters(method);
    }

    protected ASTNameList getThrowsList() {
        return MethodUtil.getThrowsList(method);
    }

    protected ASTBlock getBlock() {
        return (ASTBlock)SimpleNodeUtil.findChild(method, "net.sourceforge.pmd.ast.ASTBlock");
    }

    protected SimpleNode getReturnType() {
        return SimpleNodeUtil.findChild(method);
    }

    protected void compareModifiers(Method toMethod, Differences differences) {
        SimpleNode fromParent = getParent();
        SimpleNode toParent = toMethod.getParent();
        MethodModifiers mods = new MethodModifiers(fromParent);
        mods.diff(toParent, differences);
    }

    protected void compareParameters(Method toMethod, Differences differences) {
        ASTFormalParameters fromFormalParams = getParameters();
        ASTFormalParameters toFormalParams = toMethod.getParameters();
        compareParameters(fromFormalParams, toFormalParams, differences);
    }

    protected void compareThrows(Method toMethod, Differences differences) {
        ASTNameList fromThrowsList = getThrowsList();
        ASTNameList toThrowsList = toMethod.getThrowsList();
        compareThrows(method, fromThrowsList, toMethod.method, toThrowsList, differences);
    }

    protected void compareBodies(Method toMethod, Differences differences) {
        // tr.Ace.log("method", method);
        // tr.Ace.log("toMethod", toMethod);

        ASTBlock fromBlock = getBlock();
        ASTBlock toBlock = toMethod.getBlock();

        if (fromBlock == null) {
            if (toBlock != null) {
                differences.changed(method, toMethod.method, Messages.METHOD_BLOCK_ADDED);
            }
        }
        else if (toBlock == null) {
            differences.changed(method, toMethod.method, Messages.METHOD_BLOCK_REMOVED);
        }
        else {
            String fromName = MethodUtil.getFullName(method);
            compareBlocks(fromName, fromBlock, toBlock, differences);
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

    protected void compareBlocks(String fromName, ASTBlock fromBlock, ASTBlock toBlock, Differences differences) {
        List<Token> fromTokens = SimpleNodeUtil.getChildTokens(fromBlock);
        List<Token> toTokens = SimpleNodeUtil.getChildTokens(toBlock);
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
