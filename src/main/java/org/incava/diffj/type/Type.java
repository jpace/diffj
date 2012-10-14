package org.incava.diffj.type;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Diffable;
import org.incava.diffj.Differences;
import org.incava.diffj.Element;
import org.incava.diffj.Messages;
import org.incava.diffj.field.Fields;
import org.incava.diffj.function.Ctors;
import org.incava.diffj.function.Methods;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Type extends Element implements Diffable<Type> {
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
        Methods toMethods = toType.getMethods();
        fromMethods.diff(toMethods, differences);
        
        Fields fromFields = getFields();
        Fields toFields = toType.getFields();
        fromFields.diff(toFields, differences);
        
        Ctors fromCtors = getCtors();
        Ctors toCtors = toType.getCtors();
        fromCtors.diff(toCtors, differences);
        
        InnerTypes fromInnerTypes = getInnerTypes();
        InnerTypes toInnerTypes = toType.getInnerTypes();
        fromInnerTypes.diff(toInnerTypes, differences);
    }

    public String getName() {
        return SimpleNodeUtil.findToken(decl, JavaParserConstants.IDENTIFIER).image;
    }

    public double getMatchScore(Type toType) {
        return getName().equals(toType.getName()) ? 1.0 : 0.0;
    }

    public String getAddedMessage() {
        return isInterface() ? Messages.INNER_INTERFACE_ADDED : Messages.INNER_CLASS_ADDED;
    }

    public String getRemovedMessage() {
        return isInterface() ? Messages.INNER_INTERFACE_REMOVED : Messages.INNER_CLASS_REMOVED;
    }
}
