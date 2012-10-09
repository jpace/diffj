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

    public void diff(ASTMethodDeclaration toMethod, Differences differences) {
        compareModifiers(toMethod, differences);
        compareReturnTypes(toMethod, differences);
        compareParameters(toMethod, differences);
        compareThrows(toMethod, differences);
        compareBodies(toMethod, differences);
    }

    protected void compareModifiers(ASTMethodDeclaration toMethod, Differences differences) {
        SimpleNode fromParent = SimpleNodeUtil.getParent(method);
        SimpleNode toParent = SimpleNodeUtil.getParent(toMethod);
        MethodModifiers mods = new MethodModifiers(fromParent);
        mods.diff(toParent, differences);
    }

    protected void compareParameters(ASTMethodDeclaration toMethod, Differences differences) {
        ASTFormalParameters fromFormalParams = MethodUtil.getParameters(method);
        ASTFormalParameters toFormalParams = MethodUtil.getParameters(toMethod);
        compareParameters(fromFormalParams, toFormalParams, differences);
    }

    protected void compareThrows(ASTMethodDeclaration toMethod, Differences differences) {
        ASTNameList fromThrowsList = MethodUtil.getThrowsList(method);
        ASTNameList toThrowsList = MethodUtil.getThrowsList(toMethod);
        compareThrows(method, fromThrowsList, toMethod, toThrowsList, differences);
    }

    protected void compareBodies(ASTMethodDeclaration toMethod, Differences differences) {
        // tr.Ace.log("method", method);
        // tr.Ace.log("toMethod", toMethod);

        ASTBlock fromBlock = (ASTBlock)SimpleNodeUtil.findChild(method, "net.sourceforge.pmd.ast.ASTBlock");
        ASTBlock toBlock = (ASTBlock)SimpleNodeUtil.findChild(toMethod, "net.sourceforge.pmd.ast.ASTBlock");

        if (fromBlock == null) {
            if (toBlock != null) {
                differences.changed(method, toMethod, Messages.METHOD_BLOCK_ADDED);
            }
        }
        else if (toBlock == null) {
            differences.changed(method, toMethod, Messages.METHOD_BLOCK_REMOVED);
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
        List<Token> method = SimpleNodeUtil.getChildTokens(fromBlock);
        List<Token> toMethod = SimpleNodeUtil.getChildTokens(toBlock);
        compareCode(fromName, method, toMethod, differences);
    }

    protected void compareReturnTypes(ASTMethodDeclaration toMethod, Differences differences) {
        SimpleNode fromRetType    = SimpleNodeUtil.findChild(method);
        SimpleNode toRetType      = SimpleNodeUtil.findChild(toMethod);
        String     fromRetTypeStr = SimpleNodeUtil.toString(fromRetType);
        String     toRetTypeStr   = SimpleNodeUtil.toString(toRetType);

        if (!fromRetTypeStr.equals(toRetTypeStr)) {
            differences.changed(fromRetType, toRetType, Messages.RETURN_TYPE_CHANGED, fromRetTypeStr, toRetTypeStr);
        }
    }

}
