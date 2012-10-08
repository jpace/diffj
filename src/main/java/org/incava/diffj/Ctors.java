package org.incava.diffj;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.CtorUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Ctors extends Functions {
    public Ctors(FileDiffs differences) {
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
        
        List<Token> children = SimpleNodeUtil.getChildTokens(ctor);
        
        Iterator<Token> it = children.iterator();
        while (it.hasNext()) {
            Token tk = it.next();
            if (tk.kind == JavaParserConstants.LBRACE) {
                break;
            }
            else {
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
        compareCode(aName, aCode, bCode);
    }
}
