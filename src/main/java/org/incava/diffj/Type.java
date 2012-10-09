package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Type extends Item {
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
        compareExtends(toDecl, differences);
        compareImplements(toDecl, differences);
        compareDeclarations(toDecl, differences);
    }

    protected void compareModifiers(SimpleNode fromNode, SimpleNode toNode, Differences differences) {
        TypeModifiers typeMods = new TypeModifiers(fromNode);
        typeMods.diff(toNode, differences);
    }

    protected void compareExtends(ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        Extends ed = new Extends(decl);
        ed.diff(toDecl, differences);
    }

    protected void compareImplements(ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        Implements id = new Implements(decl);
        id.diff(toDecl, differences);
    }

    protected void compareDeclarations(ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        Methods methods = new Methods(decl, differences);
        methods.diff(toDecl, differences);
        
        Fields fields = new Fields(decl, differences);
        fields.diff(toDecl, differences);
        
        Ctors ctors = new Ctors(decl, differences);
        ctors.diff(toDecl, differences);
        
        InnerTypes innerTypes = new InnerTypes(decl, differences);
        innerTypes.diff(toDecl, differences);
    }
}
