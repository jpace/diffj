package org.incava.diffj.compunit;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.Differences;
import org.incava.diffj.Messages;
import org.incava.pmdx.CompilationUnitUtil;
import org.incava.pmdx.SimpleNodeUtil;

public class Package {
    private final ASTCompilationUnit compUnit;
    private final ASTPackageDeclaration pkg;

    public Package(ASTCompilationUnit compUnit) {
        this.compUnit = compUnit;
        this.pkg = CompilationUnitUtil.getPackage(compUnit);
    }

    public void diff(Package toPackage, Differences differences) {
        if (pkg == null) {
            if (toPackage.pkg != null) {
                ASTName    name    = toPackage.getPackageName();
                SimpleNode fromPos = getChild();
                differences.added(fromPos, name, Messages.PACKAGE_ADDED);
            }
        }
        else if (toPackage.pkg == null) {
            ASTName    name  = getPackageName();
            SimpleNode toPos = toPackage.getChild();
            differences.deleted(name, toPos, Messages.PACKAGE_REMOVED);
        }
        else {
            ASTName fromName = getPackageName();
            String  fromStr  = SimpleNodeUtil.toString(fromName);
            ASTName toName   = toPackage.getPackageName();
            String  toStr    = SimpleNodeUtil.toString(toName);

            if (!fromStr.equals(toStr)) {
                differences.changed(fromName, toName, Messages.PACKAGE_RENAMED);
            }
        }
    }

    protected SimpleNode getChild() {
        SimpleNode child = SimpleNodeUtil.findChild(compUnit);
        return child == null ? compUnit : child;
    }

    public ASTName getPackageName() {
        return (ASTName)SimpleNodeUtil.findChild(pkg, "net.sourceforge.pmd.ast.ASTName");
    }
}
