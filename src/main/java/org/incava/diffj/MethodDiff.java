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

    public void compare(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        compareModifiers(a, b);
        compareReturnTypes(a, b);
        compareParameters(a, b);
        compareThrows(a, b);
        compareBodies(a, b);
    }

    protected void compareModifiers(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        compareModifiers(SimpleNodeUtil.getParent(a), SimpleNodeUtil.getParent(b), VALID_MODIFIERS);
    }

    protected void compareParameters(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        ASTFormalParameters afp = MethodUtil.getParameters(a);
        ASTFormalParameters bfp = MethodUtil.getParameters(b);

        compareParameters(afp, bfp);
    }

    protected void compareThrows(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        ASTNameList at = MethodUtil.getThrowsList(a);
        ASTNameList bt = MethodUtil.getThrowsList(b);

        compareThrows(a, at, b, bt);
    }

    protected void compareBodies(ASTMethodDeclaration a, ASTMethodDeclaration b) {
        // tr.Ace.log("a", a);
        // tr.Ace.log("b", b);

        ASTBlock aBlock = (ASTBlock)SimpleNodeUtil.findChild(a, ASTBlock.class);
        ASTBlock bBlock = (ASTBlock)SimpleNodeUtil.findChild(b, ASTBlock.class);

        if (aBlock == null) {
            if (bBlock != null) {
                changed(a, b, METHOD_BLOCK_ADDED);
            }
        }
        else if (bBlock == null) {
            changed(a, b, METHOD_BLOCK_REMOVED);
        }
        else {
            String aName = MethodUtil.getFullName(a);
            String bName = MethodUtil.getFullName(b);
            
            compareBlocks(aName, aBlock, bName, bBlock);
        }
    }

    // protected void compareBlocks(String aName, ASTBlock aBlock, String bName, ASTBlock bBlock)
    // {
    //     tr.Ace.cyan("aBlock", aBlock);
    //     SimpleNodeUtil.dump(aBlock, "");
    //     tr.Ace.cyan("bBlock", bBlock);
    //     SimpleNodeUtil.dump(bBlock, "");
    //     // walk through, looking for common if and for statements ...
    //     tr.Ace.cyan("aChildren(null)", SimpleNodeUtil.findChildren(aBlock));
    //     tr.Ace.cyan("bChildren(null)", SimpleNodeUtil.findChildren(bBlock));        
    // }

    protected void compareBlocks(String aName, ASTBlock aBlock, String bName, ASTBlock bBlock) {
        List<Token> a = SimpleNodeUtil.getChildrenSerially(aBlock);
        List<Token> b = SimpleNodeUtil.getChildrenSerially(bBlock);

        compareCode(aName, a, bName, b);
    }
}
