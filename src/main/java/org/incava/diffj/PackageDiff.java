package org.incava.diffj;

import java.util.Collection;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class PackageDiff extends DiffComparator {
    public PackageDiff(FileDiffs differences) {
        super(differences);        
    }

    public ASTName findChildName(ASTPackageDeclaration pkg) {
        return (ASTName)SimpleNodeUtil.findChild(pkg, "net.sourceforge.pmd.ast.ASTName");
    }

    public void compare(ASTCompilationUnit a, ASTCompilationUnit b) {
        ASTPackageDeclaration aPkg = CompilationUnitUtil.getPackage(a);
        ASTPackageDeclaration bPkg = CompilationUnitUtil.getPackage(b);

        if (aPkg == null) {
            if (bPkg != null) {
                ASTName    name = findChildName(bPkg);
                SimpleNode aPos = SimpleNodeUtil.findChild(a);

                if (aPos == null) {
                    aPos = a;
                }
                added(aPos, name, Messages.PACKAGE_ADDED);
            }
        }
        else if (bPkg == null) {
            ASTName    name = findChildName(aPkg);
            SimpleNode bPos = SimpleNodeUtil.findChild(b);

            if (bPos == null) {
                bPos = b;
            }
            deleted(name, bPos, Messages.PACKAGE_REMOVED);
        }
        else {
            ASTName aName = findChildName(aPkg);
            String  aStr  = SimpleNodeUtil.toString(aName);
            ASTName bName = findChildName(bPkg);
            String  bStr  = SimpleNodeUtil.toString(bName);

            if (!aStr.equals(bStr)) {
                changed(aName, bName, Messages.PACKAGE_RENAMED);
            }
        }
    }
}
