package org.incava.diffj;

import java.util.Collection;
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

public class Method extends Functions {
    private final ASTMethodDeclaration method;

    public Method(ASTMethodDeclaration method) {
        this.method = method;
    }

    public void diff(ASTMethodDeclaration to, Differences differences) {
        compareModifiers(this.method, to, differences);
        compareReturnTypes(this.method, to, differences);
        compareParameters(this.method, to, differences);
        compareThrows(this.method, to, differences);
        compareBodies(this.method, to, differences);
    }

    protected void compareModifiers(ASTMethodDeclaration from, ASTMethodDeclaration to, Differences differences) {
        SimpleNode fromParent = SimpleNodeUtil.getParent(from);
        SimpleNode toParent = SimpleNodeUtil.getParent(to);
        MethodModifiers mods = new MethodModifiers(fromParent);
        mods.diff(toParent, differences);
    }

    protected void compareParameters(ASTMethodDeclaration from, ASTMethodDeclaration to, Differences differences) {
        ASTFormalParameters fromFormalParams = MethodUtil.getParameters(from);
        ASTFormalParameters toFormalParams = MethodUtil.getParameters(to);
        compareParameters(fromFormalParams, toFormalParams, differences);
    }

    protected void compareThrows(ASTMethodDeclaration from, ASTMethodDeclaration to, Differences differences) {
        ASTNameList fromThrowsList = MethodUtil.getThrowsList(from);
        ASTNameList toThrowsList = MethodUtil.getThrowsList(to);
        compareThrows(from, fromThrowsList, to, toThrowsList, differences);
    }

    protected void compareBodies(ASTMethodDeclaration from, ASTMethodDeclaration to, Differences differences) {
        // tr.Ace.log("from", from);
        // tr.Ace.log("to", to);

        ASTBlock fromBlock = (ASTBlock)SimpleNodeUtil.findChild(from, "net.sourceforge.pmd.ast.ASTBlock");
        ASTBlock toBlock = (ASTBlock)SimpleNodeUtil.findChild(to, "net.sourceforge.pmd.ast.ASTBlock");

        if (fromBlock == null) {
            if (toBlock != null) {
                differences.changed(from, to, Messages.METHOD_BLOCK_ADDED);
            }
        }
        else if (toBlock == null) {
            differences.changed(from, to, Messages.METHOD_BLOCK_REMOVED);
        }
        else {
            String fromName = MethodUtil.getFullName(from);
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
        List<Token> from = SimpleNodeUtil.getChildTokens(fromBlock);
        List<Token> to = SimpleNodeUtil.getChildTokens(toBlock);
        compareCode(fromName, from, to, differences);
    }
}
