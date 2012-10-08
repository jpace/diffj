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

    public void compare(ASTConstructorDeclaration from, ASTConstructorDeclaration to) {
        tr.Ace.log("from: " + from + "; to: " + to);
        
        compareParameters(from, to);
        compareThrows(from, to);
        compareBodies(from, to);
    }
    
    protected void compareParameters(ASTConstructorDeclaration from, ASTConstructorDeclaration to) {
        ASTFormalParameters fromParam = CtorUtil.getParameters(from);
        ASTFormalParameters toParams = CtorUtil.getParameters(to);
        compareParameters(fromParam, toParams);
    }

    protected void compareThrows(ASTConstructorDeclaration from, ASTConstructorDeclaration to) {
        ASTNameList fromThrows = CtorUtil.getThrowsList(from);
        ASTNameList toThrows = CtorUtil.getThrowsList(to);
        compareThrows(from, fromThrows, to, toThrows);
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

    protected void compareBodies(ASTConstructorDeclaration from, ASTConstructorDeclaration to) {
        List<Token> fromCode = getCodeSerially(from);
        List<Token> toCode = getCodeSerially(to);
        String fromName = CtorUtil.getFullName(from);
        String toName = CtorUtil.getFullName(to);
        compareCode(fromName, fromCode, toCode);
    }
}
