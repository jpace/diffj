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
    private final ASTClassOrInterfaceDeclaration decl;
    
    public Type(ASTClassOrInterfaceDeclaration decl) {
        this.decl = decl;
    }

    public void diff(ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        if (!decl.isInterface() && toDecl.isInterface()) {
            differences.changed(decl, toDecl, Messages.TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (decl.isInterface() && !toDecl.isInterface()) {
            differences.changed(decl, toDecl, Messages.TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
        }
        
        SimpleNode fromParent = SimpleNodeUtil.getParent(decl);
        SimpleNode toParent = SimpleNodeUtil.getParent(toDecl);

        compareAccess(fromParent, toParent, differences);
        compareModifiers(fromParent, toParent, differences);
        compareExtends(decl, toDecl, differences);
        compareImplements(decl, toDecl, differences);
        compareDeclarations(decl, toDecl, differences);
    }

    protected void compareModifiers(SimpleNode fromNode, SimpleNode toNode, Differences differences) {
        TypeModifiers typeMods = new TypeModifiers(fromNode);
        typeMods.diff(toNode, differences);
    }

    protected void compareExtends(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        Extends ed = new Extends(differences.getFileDiffs());
        ed.compareExtends(fromDecl, toDecl);
    }

    protected void compareImplements(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        Implements id = new Implements(differences.getFileDiffs());
        id.compareImplements(fromDecl, toDecl);
    }

    protected void compareDeclarations(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        FileDiffs diffs = differences.getFileDiffs();        
        TypeMethods tmd = new TypeMethods(diffs);
        tmd.compare(fromDecl, toDecl);
        
        TypeFields tfd = new TypeFields(diffs);
        tfd.compare(fromDecl, toDecl);
        
        TypeCtors ctd = new TypeCtors(diffs);
        ctd.compare(fromDecl, toDecl);
        
        InnerTypes titd = new InnerTypes(diffs);
        titd.compare(fromDecl, toDecl);
    }
}
