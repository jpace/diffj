package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.pmd.*;


public class CtorDiff extends FunctionDiff {

    public CtorDiff(Report report) {
        super(report);
    }

    public CtorDiff(Collection<FileDiff> differences) {
        super(differences);
    }

    public void compare(ASTConstructorDeclaration a, ASTConstructorDeclaration b) {
        tr.Ace.log("a: " + a + "; b: " + b);
        
        compareParameters(a, b);
        compareThrows(a, b);
        compareBodies(a, b);
    }
    
    protected void compareParameters(ASTConstructorDeclaration a, ASTConstructorDeclaration b) {
        ASTFormalParameters afp = CtorUtil.getParameters(a);
        ASTFormalParameters bfp = CtorUtil.getParameters(b);

        compareParameters(afp, bfp);
    }

    protected void compareThrows(ASTConstructorDeclaration a, ASTConstructorDeclaration b) {
        ASTNameList at = CtorUtil.getThrowsList(a);
        ASTNameList bt = CtorUtil.getThrowsList(b);

        compareThrows(a, at, b, bt);
    }

    protected List<Token> getCodeSerially(ASTConstructorDeclaration ctor) {
        // removes all tokens up to the first left brace. This is because ctors
        // don't have their own blocks, unlike methods.
        
        List<Token> children = SimpleNodeUtil.getChildrenSerially(ctor);
        
        Iterator<Token> it = children.iterator();
        while (it.hasNext()) {
            Token tk = it.next();
            if (tk.kind == JavaParserConstants.LBRACE) {
                // tr.Ace.log("breaking at: " + obj);
                break;
            }
            else {
                // tr.Ace.log("deleting: " + obj);
                it.remove();
            }
        }

        return children;
    }

    protected void compareBodies(ASTConstructorDeclaration a, ASTConstructorDeclaration b) {
        List<Token> aCode = getCodeSerially(a);
        List<Token> bCode = getCodeSerially(b);
        
        String aName = CtorUtil.getFullName(a);
        String bName = CtorUtil.getFullName(b);

        compareCode(aName, aCode, bName, bCode);
    }

}
