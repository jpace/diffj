package org.incava.diffj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.CtorUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Ctor extends Function {
    private final ASTConstructorDeclaration ctor;

    public Ctor(ASTConstructorDeclaration ctor) {
        this.ctor = ctor;
    }

    public void diff(ASTConstructorDeclaration toCtor, Differences differences) {
        tr.Ace.log("this.ctor: " + this.ctor + "; toCtor: " + toCtor);
        
        compareAccess(SimpleNodeUtil.getParent(ctor), SimpleNodeUtil.getParent(toCtor), differences);
        compareParameters(toCtor, differences);
        compareThrows(toCtor, differences);
        compareBodies(toCtor, differences);
    }
    
    protected void compareParameters(ASTConstructorDeclaration toCtor, Differences differences) {
        ASTFormalParameters fromParam = CtorUtil.getParameters(ctor);
        ASTFormalParameters toParams = CtorUtil.getParameters(toCtor);
        compareParameters(fromParam, toParams, differences);
    }

    protected void compareThrows(ASTConstructorDeclaration toCtor, Differences differences) {
        ASTNameList fromThrows = CtorUtil.getThrowsList(ctor);
        ASTNameList toThrows = CtorUtil.getThrowsList(toCtor);
        compareThrows(ctor, fromThrows, toCtor, toThrows, differences);
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

    protected void compareBodies(ASTConstructorDeclaration toCtor, Differences differences) {
        List<Token> fromCode = getCodeSerially(ctor);
        List<Token> toCode = getCodeSerially(toCtor);
        String fromName = CtorUtil.getFullName(ctor);
        compareCode(fromName, fromCode, toCode, differences);
    }
}
