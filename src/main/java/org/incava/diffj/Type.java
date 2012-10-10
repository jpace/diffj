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
        Type toType = new Type(toDecl);
        
        if (!isInterface() && toType.isInterface()) {
            differences.changed(decl, toType.decl, Messages.TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (isInterface() && !toType.isInterface()) {
            differences.changed(decl, toType.decl, Messages.TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
        }
        
        SimpleNode fromParent = SimpleNodeUtil.getParent(decl);
        SimpleNode toParent = SimpleNodeUtil.getParent(toDecl);

        compareAccess(fromParent, toParent, differences);
        compareModifiers(fromParent, toParent, differences);
        compareExtends(toDecl, differences);
        compareImplements(toDecl, differences);
        compareDeclarations(toDecl, differences);
    }

    protected boolean isInterface() {
        return decl.isInterface();
    }

    protected void compareModifiers(SimpleNode fromNode, SimpleNode toNode, Differences differences) {
        TypeModifiers fromMods = new TypeModifiers(fromNode);
        TypeModifiers toMods = new TypeModifiers(toNode);
        fromMods.diff(toMods, differences);
    }

    protected void compareExtends(ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        Extends fromExtends = new Extends(decl);
        Extends toExtends = new Extends(toDecl);
        fromExtends.diff(toExtends, differences);
    }

    protected void compareImplements(ASTClassOrInterfaceDeclaration toDecl, Differences differences) {
        Implements fromImplements = new Implements(decl);
        Implements toImplements = new Implements(toDecl);
        fromImplements.diff(toImplements, differences);
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
