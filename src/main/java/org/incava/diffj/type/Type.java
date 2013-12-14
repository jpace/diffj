package org.incava.diffj.type;

import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.AccessibleElement;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;
import org.incava.diffj.field.Fields;
import org.incava.diffj.function.Ctors;
import org.incava.diffj.function.Methods;
import org.incava.diffj.function.Initializers;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Type extends AccessibleElement implements Diffable<Type> {
    public static final Message TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = new Message("type changed from class to interface");
    public static final Message TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = new Message("type changed from interface to class");

    public static final Message INNER_INTERFACE_ADDED = new Message("inner interface added: {0}");
    public static final Message INNER_INTERFACE_REMOVED = new Message("inner interface removed: {0}");

    public static final Message INNER_CLASS_ADDED = new Message("inner class added: {0}");
    public static final Message INNER_CLASS_REMOVED = new Message("inner class removed: {0}");

    private final ASTClassOrInterfaceDeclaration decl;
    
    public Type(ASTClassOrInterfaceDeclaration decl) {
        super(decl);
        this.decl = decl;
    }

    public void diff(Type toType, Differences differences) {
        if (!isInterface() && toType.isInterface()) {
            differences.changed(this, toType, TYPE_CHANGED_FROM_CLASS_TO_INTERFACE);
        }
        else if (isInterface() && !toType.isInterface()) {
            differences.changed(this, toType, TYPE_CHANGED_FROM_INTERFACE_TO_CLASS);
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

    public <ItemType extends SimpleNode> List<ItemType> getDeclarationsOfClass(Class<ItemType> cls) {
        TypeDeclarationList tdl = new TypeDeclarationList(decl);
        return tdl.getDeclarationsOfClass(cls);
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

    protected Initializers getInitializers() {
        return new Initializers(decl);
    }

    protected void compareModifiers(Type toType, Differences differences) {
        TypeModifiers fromMods = getModifiers();
        fromMods.diff(toType.getModifiers(), differences);
    }

    protected void compareExtends(Type toType, Differences differences) {
        Extends fromExtends = getExtends();
        fromExtends.diff(toType.getExtends(), differences);
    }

    protected void compareImplements(Type toType, Differences differences) {
        Implements fromImplements = getImplements();
        fromImplements.diff(toType.getImplements(), differences);
    }

    protected void compareDeclarations(Type toType, Differences differences) {
        Methods fromMethods = getMethods();
        fromMethods.diff(toType.getMethods(), differences);
        
        Fields fromFields = getFields();
        fromFields.diff(toType.getFields(), differences);
        
        Ctors fromCtors = getCtors();
        fromCtors.diff(toType.getCtors(), differences);
        
        InnerTypes fromInnerTypes = getInnerTypes();
        fromInnerTypes.diff(toType.getInnerTypes(), differences);

        Initializers fromInits = getInitializers();
        fromInits.diff(toType.getInitializers(), differences);
    }

    public String getName() {
        return SimpleNodeUtil.findToken(decl, JavaParserConstants.IDENTIFIER).image;
    }

    public double getMatchScore(Type toType) {
        return getName().equals(toType.getName()) ? 1.0 : 0.0;
    }

    public Message getAddedMessage() {
        return isInterface() ? INNER_INTERFACE_ADDED : INNER_CLASS_ADDED;
    }

    public Message getRemovedMessage() {
        return isInterface() ? INNER_INTERFACE_REMOVED : INNER_CLASS_REMOVED;
    }
}
