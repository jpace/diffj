package org.incava.diffj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.CtorUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Ctor extends Function {
    private final ASTConstructorDeclaration ctor;

    public Ctor(ASTConstructorDeclaration ctor) {
        super(ctor);
        this.ctor = ctor;
    }

    public void diff(Ctor toCtor, Differences differences) {
        compareAccess(toCtor, differences);
        compareParameters(toCtor, differences);
        compareThrows(toCtor, differences);
        compareBodies(toCtor, differences);
    }

    protected ASTFormalParameters getParameters() {
        return CtorUtil.getParameters(ctor);
    }

    protected ASTNameList getThrowsList() {
        return CtorUtil.getThrowsList(ctor);
    }

    protected String getName() {
        return CtorUtil.getFullName(ctor);
    }
    
    protected void compareParameters(Ctor toCtor, Differences differences) {
        ASTFormalParameters fromParam = getParameters();
        ASTFormalParameters toParams = toCtor.getParameters();
        compareParameters(fromParam, toParams, differences);
    }

    protected List<Token> getCodeTokens() {
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

    protected void compareBodies(Ctor toCtor, Differences differences) {
        List<Token> fromCode = getCodeTokens();
        List<Token> toCode = toCtor.getCodeTokens();
        String fromName = getName();
        compareCode(fromName, fromCode, toCode, differences);
    }
}
