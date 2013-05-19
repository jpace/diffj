package org.incava.diffj.type;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.AccessibleElement;
import org.incava.diffj.element.Diffable;
import org.incava.diffj.element.Differences;
import org.incava.diffj.field.Field;
import org.incava.diffj.function.Ctor;
import org.incava.diffj.function.Method;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.TypeDeclarationUtil;

public class Type extends AccessibleElement implements Diffable<Type> {
    public static final String TYPE_CHANGED_FROM_CLASS_TO_INTERFACE = "type changed from class to interface";
    public static final String TYPE_CHANGED_FROM_INTERFACE_TO_CLASS = "type changed from interface to class";

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
        List<ASTClassOrInterfaceBodyDeclaration> decls = TypeDeclarationUtil.getDeclarations(decl);
        return getDeclarationsOfClass(decls, cls);
    }

    public <ItemType extends SimpleNode> List<ItemType> getDeclarationsOfClass(List<ASTClassOrInterfaceBodyDeclaration> decls, Class<ItemType> cls) {
        List<ItemType> declList = new ArrayList<ItemType>();

        for (ASTClassOrInterfaceBodyDeclaration decl : decls) {
            ItemType dec = TypeDeclarationUtil.getDeclaration(decl, cls);

            if (dec != null) {
                declList.add(dec);
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

    protected Items<Method, ASTMethodDeclaration> getMethods() {
        return new Items<Method, ASTMethodDeclaration>(decl, net.sourceforge.pmd.ast.ASTMethodDeclaration.class) {
            public Method getAstType(ASTMethodDeclaration methodDecl) {
                return new Method(methodDecl);
            }
        };
    }

    protected Items<Field, ASTFieldDeclaration> getFields() {
        return new Items<Field, ASTFieldDeclaration>(decl, net.sourceforge.pmd.ast.ASTFieldDeclaration.class) {
            public Field getAstType(ASTFieldDeclaration fieldDecl) {
                return new Field(fieldDecl);
            }
        };
    }

    protected Items<Ctor, ASTConstructorDeclaration> getCtors() {
        return new Items<Ctor, ASTConstructorDeclaration>(decl, net.sourceforge.pmd.ast.ASTConstructorDeclaration.class) {
            public Ctor getAstType(ASTConstructorDeclaration ctorDecl) {
                return new Ctor(ctorDecl);
            }
        };
    }
            
    protected Items<Type, ASTClassOrInterfaceDeclaration> getInnerTypes() {
        return new Items<Type, ASTClassOrInterfaceDeclaration>(decl, net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration.class) {
            public Type getAstType(ASTClassOrInterfaceDeclaration decl) {
                return new Type(decl);
            }
        };
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
        Items<Method, ASTMethodDeclaration> fromMethods = getMethods();
        Items<Method, ASTMethodDeclaration> toMethods = toType.getMethods();
        fromMethods.diff(toMethods, differences);
        
        Items<Field, ASTFieldDeclaration> fromFields = getFields();
        Items<Field, ASTFieldDeclaration> toFields = toType.getFields();
        fromFields.diff(toFields, differences);
        
        Items<Ctor, ASTConstructorDeclaration> fromCtors = getCtors();
        Items<Ctor, ASTConstructorDeclaration> toCtors = toType.getCtors();
        fromCtors.diff(toCtors, differences);
        
        Items<Type, ASTClassOrInterfaceDeclaration> fromInnerTypes = getInnerTypes();
        Items<Type, ASTClassOrInterfaceDeclaration> toInnerTypes = toType.getInnerTypes();
        fromInnerTypes.diff(toInnerTypes, differences);
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
