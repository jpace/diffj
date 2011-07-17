package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for classs.
 */
public class ClassUtil extends SimpleNodeUtil {
    public static Token getName(ASTClassOrInterfaceDeclaration coid) {
        return findToken(coid, JavaParserConstants.IDENTIFIER);
    }

    public static double getMatchScore(ASTClassOrInterfaceDeclaration a, ASTClassOrInterfaceDeclaration b) {
        return getName(a).image.equals(getName(b).image) ? 1.0 : 0.0;
    }

}

