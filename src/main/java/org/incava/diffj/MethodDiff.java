package org.incava.diffj;

import java.util.Collection;
import java.util.List;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.MethodUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class MethodDiff extends FunctionDiff {
    public static final String METHOD_BLOCK_ADDED = "method block added";
    public static final String METHOD_BLOCK_REMOVED = "method block removed";

    protected static final int[] VALID_MODIFIERS = new int[] {
        JavaParserConstants.ABSTRACT,
        JavaParserConstants.FINAL,
        JavaParserConstants.NATIVE,
        JavaParserConstants.STATIC,
        JavaParserConstants.STRICTFP
    };

    public MethodDiff(FileDiffs differences) {
        super(differences);
    }

    public void compare(ASTMethodDeclaration from, ASTMethodDeclaration to) {
        compareModifiers(from, to);
        compareReturnTypes(from, to);
        compareParameters(from, to);
        compareThrows(from, to);
        compareBodies(from, to);
    }

    protected void compareModifiers(ASTMethodDeclaration from, ASTMethodDeclaration to) {
        compareModifiers(SimpleNodeUtil.getParent(from), SimpleNodeUtil.getParent(to), VALID_MODIFIERS);
    }

    protected void compareParameters(ASTMethodDeclaration from, ASTMethodDeclaration to) {
        ASTFormalParameters fromFormalParams = MethodUtil.getParameters(from);
        ASTFormalParameters toFormalParams = MethodUtil.getParameters(to);

        compareParameters(fromFormalParams, toFormalParams);
    }

    protected void compareThrows(ASTMethodDeclaration from, ASTMethodDeclaration to) {
        ASTNameList fromThrowsList = MethodUtil.getThrowsList(from);
        ASTNameList toThrowsList = MethodUtil.getThrowsList(to);

        compareThrows(from, fromThrowsList, to, toThrowsList);
    }

    protected void compareBodies(ASTMethodDeclaration from, ASTMethodDeclaration to) {
        // tr.Ace.log("from", from);
        // tr.Ace.log("to", to);

        ASTBlock fromBlock = (ASTBlock)SimpleNodeUtil.findChild(from, "net.sourceforge.pmd.ast.ASTBlock");
        ASTBlock toBlock = (ASTBlock)SimpleNodeUtil.findChild(to, "net.sourceforge.pmd.ast.ASTBlock");

        if (fromBlock == null) {
            if (toBlock != null) {
                changed(from, to, METHOD_BLOCK_ADDED);
            }
        }
        else if (toBlock == null) {
            changed(from, to, METHOD_BLOCK_REMOVED);
        }
        else {
            String fromName = MethodUtil.getFullName(from);
            String toName = MethodUtil.getFullName(to);
            
            compareBlocks(fromName, fromBlock, toName, toBlock);
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

    protected void compareBlocks(String fromName, ASTBlock fromBlock, String toName, ASTBlock toBlock) {
        List<Token> from = SimpleNodeUtil.getChildrenSerially(fromBlock);
        List<Token> to = SimpleNodeUtil.getChildrenSerially(toBlock);

        compareCode(fromName, from, toName, to);
    }
}
