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

    public void compare(ASTTypeDeclaration fromType, ASTTypeDeclaration toType) {
        // should have only one child, the type itself, either an interface or fromType
        // class declaration

        ASTClassOrInterfaceDeclaration fromDecl = TypeDeclarationUtil.getType(fromType);
        ASTClassOrInterfaceDeclaration toDecl = TypeDeclarationUtil.getType(toType);

        if (fromDecl == null && toDecl == null) {
            tr.Ace.log("skipping 'semicolon declarations'");
        }
        else {
            compare(fromDecl, toDecl);
        }
    }

    public void compare(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl) {
        if (!fromDecl.isInterface() && toDecl.isInterface()) {
            differences.changed(fromDecl, toDecl, Messages.TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (fromDecl.isInterface() && !toDecl.isInterface()) {
            differences.changed(fromDecl, toDecl, Messages.TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
        }
        
        SimpleNode atParent = SimpleNodeUtil.getParent(fromDecl);
        SimpleNode btParent = SimpleNodeUtil.getParent(toDecl);

        compareAccess(atParent, btParent, differences);
        compareModifiers(atParent, btParent);
        compareExtends(fromDecl, toDecl);
        compareImplements(fromDecl, toDecl);
        compareDeclarations(fromDecl, toDecl);
    }

    protected void compareModifiers(SimpleNode fromNode, SimpleNode toNode) {
        TypeModifiers typeMods = new TypeModifiers(fromNode);
        typeMods.diff(toNode, differences);
    }

    protected void compareExtends(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl) {
        Extends ed = new Extends(differences.getFileDiffs());
        ed.compareExtends(fromDecl, toDecl);
    }

    protected void compareImplements(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl) {
        Implements id = new Implements(differences.getFileDiffs());
        id.compareImplements(fromDecl, toDecl);
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
