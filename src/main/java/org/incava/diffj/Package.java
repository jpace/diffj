package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Package {
    private final ASTCompilationUnit compUnit;

    public Package(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void diff(ASTCompilationUnit toCompUnit, Differences differences) {
        ASTPackageDeclaration fromPkg = CompilationUnitUtil.getPackage(compUnit);
        ASTPackageDeclaration toPkg = CompilationUnitUtil.getPackage(toCompUnit);

        if (fromPkg == null) {
            if (toPkg != null) {
                ASTName    name = findChildName(toPkg);
                SimpleNode fromPos = SimpleNodeUtil.findChild(compUnit);

                if (fromPos == null) {
                    fromPos = compUnit;
                }
                differences.added(fromPos, name, Messages.PACKAGE_ADDED);
            }
        }
        else if (toPkg == null) {
            ASTName    name = findChildName(fromPkg);
            SimpleNode toPos = SimpleNodeUtil.findChild(toCompUnit);

            if (toPos == null) {
                toPos = toCompUnit;
            }
            differences.deleted(name, toPos, Messages.PACKAGE_REMOVED);
        }
        else {
            ASTName fromName = findChildName(fromPkg);
            String  fromStr  = SimpleNodeUtil.toString(fromName);
            ASTName toName = findChildName(toPkg);
            String  toStr  = SimpleNodeUtil.toString(toName);

            if (!fromStr.equals(toStr)) {
                differences.changed(fromName, toName, Messages.PACKAGE_RENAMED);
            }
        }
    }

    public ASTName findChildName(ASTPackageDeclaration pkg) {
        return (ASTName)SimpleNodeUtil.findChild(pkg, "net.sourceforge.pmd.ast.ASTName");
    }
}
