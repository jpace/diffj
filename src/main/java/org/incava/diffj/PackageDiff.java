package org.incava.diffj;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.java.*;
import org.incava.pmd.*;


public class PackageDiff extends DiffComparator {

    public static final String PACKAGE_REMOVED = "package removed: {0}";

    public static final String PACKAGE_ADDED = "package added: {0}";

    public static final String PACKAGE_RENAMED = "package renamed from {0} to {1}";

    public PackageDiff(Collection<FileDiff> differences) {
        super(differences);        
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        ASTPackageDeclaration aPkg = CompilationUnitUtil.getPackage(a);
        ASTPackageDeclaration bPkg = CompilationUnitUtil.getPackage(b);

        if (aPkg == null) {
            if (bPkg != null) {
                ASTName    name = (ASTName)SimpleNodeUtil.findChild(bPkg, ASTName.class);
                SimpleNode aPos = SimpleNodeUtil.findChild(a, null);

                if (aPos == null) {
                    aPos = a;
                }
                added(aPos, name, PACKAGE_ADDED);
            }
        }
        else if (bPkg == null) {
            ASTName    name = (ASTName)SimpleNodeUtil.findChild(aPkg, ASTName.class);
            SimpleNode bPos = SimpleNodeUtil.findChild(b, null);

            if (bPos == null) {
                bPos = b;
            }
            deleted(name, bPos, PACKAGE_REMOVED);
        }
        else {
            ASTName aName = (ASTName)SimpleNodeUtil.findChild(aPkg, ASTName.class);
            String  aStr  = SimpleNodeUtil.toString(aName);
            ASTName bName = (ASTName)SimpleNodeUtil.findChild(bPkg, ASTName.class);
            String  bStr  = SimpleNodeUtil.toString(bName);

            if (!aStr.equals(bStr)) {
                changed(aName, bName, PACKAGE_RENAMED);
            }
        }
    }
    
}
