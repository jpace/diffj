package org.incava.diffj;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Type extends Element {
    private final ASTClassOrInterfaceDeclaration decl;
    
    public Type(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
        this.decl = decl;
    }

    public void diff(Type toType, Differences differences) {
        if (!isInterface() && toType.isInterface()) {
            differences.changed(decl, toType.decl, Messages.TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (isInterface() && !toType.isInterface()) {
            differences.changed(decl, toType.decl, Messages.TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
        }
        
        compareAccess(toType, differences);
        compareModifiers(toType, differences);
        compareExtends(toType, differences);
        compareImplements(toType, differences);
        compareDeclarations(toType, differences);
    }

    public ASTClassOrInterfaceDeclaration getDeclaration() {
        return decl;
    }

    public <ItemType extends SimpleNode> List<ItemType> getDeclarationsOfClassType(String clsName) {
        List<ASTClassOrInterfaceBodyDeclaration> decls = TypeDeclarationUtil.getDeclarations(decl);
        return getDeclarationsOfClass(decls, clsName);
    }

    @SuppressWarnings("unchecked")
    public <ItemType extends SimpleNode> List<ItemType> getDeclarationsOfClass(List<ASTClassOrInterfaceBodyDeclaration> decls, String clsName) {
        List<ItemType> declList = new ArrayList<ItemType>();

        for (ASTClassOrInterfaceBodyDeclaration decl : decls) {
            SimpleNode dec = TypeDeclarationUtil.getDeclaration(decl, clsName);

            if (dec != null) {
                declList.add((ItemType)dec);
            }   
        }
        
        return declList;
    }

    protected boolean isInterface() {
        return decl.isInterface();
    }

    protected TypeModifiers getModifiers() {
        return new TypeModifiers(getParent());
    }

    protected Extends getExtends() {
        return new Extends(decl);
    }

    protected Implements getImplements() {
        return new Implements(decl);
    }

    protected Methods getMethods() {
        return new Methods(decl);
    }

    protected Fields getFields() {
        return new Fields(decl);
    }

    protected Ctors getCtors() {
        return new Ctors(decl);
    }

    protected InnerTypes getInnerTypes() {
        return new InnerTypes(decl);
    }

    protected void compareModifiers(Type toType, Differences differences) {
        TypeModifiers fromMods = getModifiers();
        TypeModifiers toMods = toType.getModifiers();
        fromMods.diff(toMods, differences);
    }

    protected void compareExtends(Type toType, Differences differences) {
        Extends fromExtends = getExtends();
        Extends toExtends = toType.getExtends();
        fromExtends.diff(toExtends, differences);
    }

    protected void compareImplements(Type toType, Differences differences) {
        Implements fromImplements = getImplements();
        Implements toImplements = toType.getImplements();
        fromImplements.diff(toImplements, differences);
    }

    protected void compareDeclarations(Type toType, Differences differences) {
        Methods fromMethods = getMethods();
        fromMethods.diff(toType, differences);
        
        Fields fromFields = getFields();
        fromFields.diff(toType, differences);
        
        Ctors fromCtors = getCtors();
        fromCtors.diff(toType, differences);
        
        InnerTypes fromInnerTypes = getInnerTypes();
        fromInnerTypes.diff(toType, differences);
    }

    public Token getName() {
        return SimpleNodeUtil.findToken(decl, JavaParserConstants.IDENTIFIER);
    }

    public double getMatchScore(Type toType) {
        return getName().image.equals(toType.getName().image) ? 1.0 : 0.0;
    }
}
