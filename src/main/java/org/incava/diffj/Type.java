package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Type extends Items {
    public Type(FileDiffs differences) {
        super(differences);
    }

    public void compare(ASTTypeDeclaration a, ASTTypeDeclaration b) {
        // should have only one child, the type itself, either an interface or a
        // class declaration

        ASTClassOrInterfaceDeclaration at = TypeDeclarationUtil.getType(a);
        ASTClassOrInterfaceDeclaration bt = TypeDeclarationUtil.getType(b);

        if (at == null && bt == null) {
            tr.Ace.log("skipping 'semicolon declarations'");
        }
        else {
            compare(at, bt);
        }
    }

    public void compare(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        if (!at.isInterface() && bt.isInterface()) {
            differences.changed(at, bt, Messages.TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (at.isInterface() && !bt.isInterface()) {
            differences.changed(at, bt, Messages.TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
        }
        
        SimpleNode atParent = SimpleNodeUtil.getParent(at);
        SimpleNode btParent = SimpleNodeUtil.getParent(bt);

        compareAccess(atParent, btParent);
        compareModifiers(atParent, btParent);
        compareExtends(at, bt);
        compareImplements(at, bt);
        compareDeclarations(at, bt);
    }

    protected void compareModifiers(SimpleNode fromNode, SimpleNode toNode) {
        TypeModifiers typeMods = new TypeModifiers(fromNode);
        typeMods.diff(toNode, differences);
    }

    protected void compareExtends(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        Extends ed = new Extends(differences.getFileDiffs());
        ed.compareExtends(at, bt);
    }

    protected void compareImplements(ASTClassOrInterfaceDeclaration at, ASTClassOrInterfaceDeclaration bt) {
        Implements id = new Implements(differences.getFileDiffs());
        id.compareImplements(at, bt);
    }

    protected void compareDeclarations(ASTClassOrInterfaceDeclaration aNode, ASTClassOrInterfaceDeclaration bNode) {
        FileDiffs diffs = differences.getFileDiffs();        
        TypeMethods tmd = new TypeMethods(diffs);
        tmd.compare(aNode, bNode);
        
        TypeFields tfd = new TypeFields(diffs);
        tfd.compare(aNode, bNode);
        
        TypeCtors ctd = new TypeCtors(diffs);
        ctd.compare(aNode, bNode);
        
        InnerTypes titd = new InnerTypes(diffs, this);
        titd.compare(aNode, bNode);
    }
}
