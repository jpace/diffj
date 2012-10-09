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
    private final ASTClassOrInterfaceDeclaration fromDecl;
    
    public Type(ASTClassOrInterfaceDeclaration decl) {
        this.fromDecl = decl;
    }

    public void diff(ASTTypeDeclaration fromType, ASTTypeDeclaration toType, Differences differences) {
         // should have only one child, the type itself, either an interface or type
         // class declaration

         ASTClassOrInterfaceDeclaration fromDecl = TypeDeclarationUtil.getType(fromType);
         ASTClassOrInterfaceDeclaration toDecl = TypeDeclarationUtil.getType(toType);

         if (fromDecl == null && toDecl == null) {
             tr.Ace.onRed("skipping 'semicolon declarations'");
         }
         else {
             diff(fromDecl, toDecl, differences);
         }
     }

    public void diff(ASTClassOrInterfaceDeclaration fromDecl, ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        if (!fromDecl.isInterface() && toDecl.isInterface()) {
            differences.changed(fromDecl, toDecl, Messages.TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (fromDecl.isInterface() && !toDecl.isInterface()) {
            differences.changed(fromDecl, toDecl, Messages.TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
        }
        
        SimpleNode atParent = SimpleNodeUtil.getParent(fromDecl);
        SimpleNode btParent = SimpleNodeUtil.getParent(toDecl);

        compareAccess(atParent, btParent, differences);
        compareModifiers(atParent, btParent, differences);
        compareExtends(fromDecl, toDecl, differences);
        compareImplements(fromDecl, toDecl, differences);
        compareDeclarations(fromDecl, toDecl, differences);
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
        
        InnerTypes titd = new InnerTypes(diffs, this);
        titd.compare(fromDecl, toDecl);
    }
}
