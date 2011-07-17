package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for constructors.
 */
public class CtorUtil extends FunctionUtil {
    public static Token getName(ASTConstructorDeclaration ctor) {
        Token nameTk = SimpleNodeUtil.findToken(ctor, JavaParserConstants.IDENTIFIER);
        return nameTk;
    }

    public static ASTFormalParameters getParameters(ASTConstructorDeclaration ctor) {
        ASTFormalParameters params = (ASTFormalParameters)ctor.jjtGetChild(0);
        return params;
    }
    
    public static double getMatchScore(ASTConstructorDeclaration a, ASTConstructorDeclaration b) {
        ASTFormalParameters afp = getParameters(a);
        ASTFormalParameters bfp = getParameters(b);
        
        return ParameterUtil.getMatchScore(afp, bfp);
    }

    public static String getFullName(ASTConstructorDeclaration ctor) {
        Token nameTk = getName(ctor);
        ASTFormalParameters params = getParameters(ctor);
        String fullName = toFullName(nameTk, params);
        return fullName;
    }

}
