package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for compilation units.
 */
public class CompilationUnitUtil {
    public static ASTPackageDeclaration getPackage(ASTCompilationUnit cu) {
        return (ASTPackageDeclaration)SimpleNodeUtil.findChild(cu, ASTPackageDeclaration.class);
    }

    public static ASTImportDeclaration[] getImports(ASTCompilationUnit cu) {
        return (ASTImportDeclaration[])SimpleNodeUtil.findChildren(cu, ASTImportDeclaration.class);
    }

    public static ASTTypeDeclaration[] getTypeDeclarations(ASTCompilationUnit cu) {
        return (ASTTypeDeclaration[])SimpleNodeUtil.findChildren(cu, ASTTypeDeclaration.class);
    }

}
