package org.incava.diffj.type;

import org.incava.analysis.FileDiffAdd;
import org.incava.analysis.FileDiffChange;
import org.incava.analysis.FileDiffDelete;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.util.Lines;
import static org.incava.diffj.type.Extends.*;
import static org.incava.diffj.type.Implements.*;
import static org.incava.diffj.type.Type.*;

public class TestType extends ItemsTest {
    public TestType(String name) {
        super(name);
    }

    public void testClassToInterface() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("interface Test {",
                           "}"),
                 
                 new FileDiffChange(TYPE_CHANGED_FROM_CLASS_TO_INTERFACE.format(), locrg(1, 1, 2, 1), locrg(1, 1, 2, 1)));
    }

    public void testInterfaceToClass() {
        evaluate(new Lines("interface Test {",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 new FileDiffChange(TYPE_CHANGED_FROM_INTERFACE_TO_CLASS.format(), locrg(1, 1, 2, 1), locrg(1, 1, 2, 1)));
    }

    public void testClassAccessChanged() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("public class Test {",
                           "}"),

                 makeAccessAddedRef(locrg(1, 1, 5), locrg(1, 1, 6), "public"));
        
        evaluate(new Lines("public class Test {",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 makeAccessRemovedRef(locrg(1, 1, 6), locrg(1, 1, 5), "public"));
    }

    public void testClassModifierAdded() {
        evaluate(new Lines("public class Test {",
                           "}"),

                 new Lines("abstract public class Test {",
                           "}"),
                 
                 makeChangedRef(locrg(1, 1, 6), locrg(1, 1, 8), MODIFIER_MSGS, null, "abstract"));

        evaluate(new Lines("public class Test {",
                           "}"),

                 new Lines("final public class Test {",
                           "}"),
                 
                 makeChangedRef(locrg(1, 1, 6), locrg(1, 1, 5), MODIFIER_MSGS, null, "final"));

        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("strictfp class Test {",
                           "}"),
                 
                 makeChangedRef(locrg(1, 1, 5), locrg(1, 1, 8), MODIFIER_MSGS, null, "strictfp"));
    }

    public void testClassModifierRemoved() {
        evaluate(new Lines("abstract public class Test {",
                           "}"),

                 new Lines("public class Test {",
                           "}"),
                 
                 makeChangedRef(locrg(1, 1, 8), locrg(1, 1, 6), MODIFIER_MSGS, "abstract", null));

        evaluate(new Lines("final public class Test {",
                           "}"),

                 new Lines("public class Test {",
                           "}"),
                 
                 makeChangedRef(locrg(1, 1, 5), locrg(1, 1, 6), MODIFIER_MSGS, "final", null));

        evaluate(new Lines("strictfp class Test {",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 makeChangedRef(locrg(1, 1, 8), locrg(1, 1, 5), MODIFIER_MSGS, "strictfp", null));
    }

    public void testClassInnerInterfaceUnchanged() {
        evaluate(new Lines("class Test {",
                           "    interface ITest {}",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    interface ITest {}",
                           "}"),

                 NO_CHANGES);
    }

    public void testClassInnerInterfaceAdded() {
        evaluate(new Lines("class Test {",
                           "",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "    interface ITest {}",
                           "}"),
                 
                 new FileDiffAdd(locrg(1, 1, 3, 1), locrg(3, 5, 22), INNER_INTERFACE_ADDED, "ITest"));
    }

    public void testClassInnerInterfaceRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    interface ITest {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 new FileDiffDelete(locrg(3, 5, 22), locrg(1, 1, 3, 1), Type.INNER_INTERFACE_REMOVED, "ITest"));
    }

    public void testSemicolonDeclarationRemoved() {
        // Is this really a change? I don't think so.
        evaluate(new Lines("class Test {",
                           "    ;",
                           "}"),

                 new Lines("class Test {",
                           "}"),

                 NO_CHANGES);
    }

    public void testClassExtendsAdded() {
        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A extends Date {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 1, 2, 1), locrg(1, 17, 1, 20), EXTENDED_TYPE_ADDED, "Date"));

        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A extends java.util.Date {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 1, 2, 1), locrg(1, 17, 1, 30), EXTENDED_TYPE_ADDED, "java.util.Date"));
    }

    public void testClassExtendsChanged() {
        // Thanks to Pat for finding and reporting this.
        evaluate(new Lines("class A extends Object {",
                           "}"),

                 new Lines("class A extends Date {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 17, 22), locrg(1, 17, 20), EXTENDED_TYPE_CHANGED, "Object", "Date"));
    }

    public void testClassExtendsDeleted() {
        evaluate(new Lines("class A extends Date {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 17, 20), locrg(1, 1, 2, 1), EXTENDED_TYPE_REMOVED, "Date"));

        evaluate(new Lines("class A extends java.util.Date {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 17, 30), locrg(1, 1, 2, 1), EXTENDED_TYPE_REMOVED, "java.util.Date"));
    }

    public void testInterfaceExtendsAdded() {
        evaluate(new Lines("interface A {",
                           "}"),

                 new Lines("interface A extends Comparator {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 1, 2, 1), locrg(1, 21, 30), EXTENDED_TYPE_ADDED, "Comparator"));
    }

    public void testInterfaceExtendsChanged() {
        evaluate(new Lines("interface A extends Comparable {",
                           "}"),

                 new Lines("interface A extends Comparator {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 21, 30), locrg(1, 21, 30), EXTENDED_TYPE_CHANGED, "Comparable", "Comparator"));
    }

    public void testInterfaceExtendsDeleted() {
        evaluate(new Lines("interface A extends Comparable {",
                           "}"),

                 new Lines("interface A {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 21, 30), locrg(1, 1, 2, 1), EXTENDED_TYPE_REMOVED, "Comparable"));
    }

    public void testClassImplementsAdded() {
        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A implements Runnable {",
                           "}"),

                 makeCodeChangedRef(locrg(1, 1, 2, 1), locrg(1, 20, 27), IMPLEMENTED_TYPE_ADDED, "Runnable"));

        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A implements java.lang.Runnable {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 1, 2, 1), locrg(1, 20, 37), IMPLEMENTED_TYPE_ADDED, "java.lang.Runnable"));
    }

    public void testClassImplementsChanged() {
        // Thanks to Pat for finding and reporting this.
        evaluate(new Lines("class A implements Cloneable {",
                           "}"),

                 new Lines("class A implements Runnable {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 20, 28), locrg(1, 20, 27), IMPLEMENTED_TYPE_CHANGED, "Cloneable", "Runnable"));
    }

    public void testClassImplementsNoChange() {
        // Thanks to Pat for finding and reporting this.
        evaluate(new Lines("class A implements Cloneable {",
                           "}"),

                 new Lines("class A implements Cloneable {",
                           "}"),

                 NO_CHANGES);
    }

    public void testClassImplementsDeleted() {
        evaluate(new Lines("class A implements Runnable {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 20, 27), locrg(1, 1, 2, 1), IMPLEMENTED_TYPE_REMOVED, "Runnable"));

        evaluate(new Lines("class A implements java.lang.Runnable {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(locrg(1, 20, 37), locrg(1, 1, 2, 1), IMPLEMENTED_TYPE_REMOVED, "java.lang.Runnable"));
    }
}
