package org.incava.diffj;

import org.incava.analysis.FileDiffChange;

public class TestType extends ItemsTest {
    public TestType(String name) {
        super(name);
    }

    public void testClassToInterface() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("interface Test {",
                           "}"),
                 
                 new FileDiffChange(Messages.TYPE_CHANGED_FROM_CLASS_TO_INTERFACE, loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1)));
    }

    public void testInterfaceToClass() {
        evaluate(new Lines("interface Test {",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 new FileDiffChange(Messages.TYPE_CHANGED_FROM_INTERFACE_TO_CLASS, loc(1, 1), loc(2, 1), loc(1, 1), loc(2, 1)));
    }

    public void testClassAccessChanged() {
        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("public class Test {",
                           "}"),

                 makeAccessRef(null, "public", loc(1, 1), loc(1, 5), loc(1, 1), loc(1, 6)));
        
        evaluate(new Lines("public class Test {",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 makeAccessRef("public", null, loc(1, 1), loc(1, 6), loc(1, 1), loc(1, 5)));
    }

    public void testClassModifierAdded() {
        evaluate(new Lines("public class Test {",
                           "}"),

                 new Lines("abstract public class Test {",
                           "}"),
                 
                 makeChangedRef(null, "abstract", MODIFIER_MSGS, loc(1, 1), loc(1, 6), loc(1, 1), loc(1, 8)));

        evaluate(new Lines("public class Test {",
                           "}"),

                 new Lines("final public class Test {",
                           "}"),
                 
                 makeChangedRef(null, "final", MODIFIER_MSGS, loc(1, 1), loc(1, 6), loc(1, 1), loc(1, 5)));

        evaluate(new Lines("class Test {",
                           "}"),

                 new Lines("strictfp class Test {",
                           "}"),
                 
                 makeChangedRef(null, "strictfp", MODIFIER_MSGS, loc(1, 1), loc(1, 5), loc(1, 1), loc(1, 8)));
    }

    public void testClassModifierRemoved() {
        evaluate(new Lines("abstract public class Test {",
                           "}"),

                 new Lines("public class Test {",
                           "}"),
                 
                 makeChangedRef("abstract", null, MODIFIER_MSGS, loc(1, 1), loc(1, 8), loc(1, 1), loc(1, 6)));

        evaluate(new Lines("final public class Test {",
                           "}"),

                 new Lines("public class Test {",
                           "}"),
                 
                 makeChangedRef("final", null, MODIFIER_MSGS, loc(1, 1), loc(1, 5), loc(1, 1), loc(1, 6)));

        evaluate(new Lines("strictfp class Test {",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 makeChangedRef("strictfp", null, MODIFIER_MSGS, loc(1, 1), loc(1, 8), loc(1, 1), loc(1, 5)));
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
                 
                 makeInterfaceRef(null, "ITest", loc(1, 1), loc(3, 1), loc(3, 5), loc(3, 22)));
    }

    public void testClassInnerInterfaceRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    interface ITest {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeInterfaceRef("ITest", null, loc(3, 5), loc(3, 22), loc(1, 1), loc(3, 1)));
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
                 
                 makeCodeChangedRef(Messages.EXTENDED_TYPE_ADDED, "Date", loc(1, 1), loc(2, 1), loc(1, 17), loc(1, 20)));

        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A extends java.util.Date {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.EXTENDED_TYPE_ADDED, "java.util.Date", loc(1, 1), loc(2, 1), loc(1, 17), loc(1, 30)));
    }

    public void testClassExtendsChanged() {
        // Thanks to Pat for finding and reporting this.
        evaluate(new Lines("class A extends Object {",
                           "}"),

                 new Lines("class A extends Date {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.EXTENDED_TYPE_CHANGED, new String[] { "Object", "Date" }, loc(1, 17), loc(1, 22), loc(1, 17), loc(1, 20)));
    }

    public void testClassExtendsDeleted() {
        evaluate(new Lines("class A extends Date {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.EXTENDED_TYPE_REMOVED, "Date", loc(1, 17), loc(1, 20), loc(1, 1), loc(2, 1)));

        evaluate(new Lines("class A extends java.util.Date {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.EXTENDED_TYPE_REMOVED, "java.util.Date", loc(1, 17), loc(1, 30), loc(1, 1), loc(2, 1)));
    }

    public void testInterfaceExtendsAdded() {
        evaluate(new Lines("interface A {",
                           "}"),

                 new Lines("interface A extends Comparator {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.EXTENDED_TYPE_ADDED, "Comparator", loc(1, 1), loc(2, 1), loc(1, 21), loc(1, 30)));
    }

    public void testInterfaceExtendsChanged() {
        evaluate(new Lines("interface A extends Comparable {",
                           "}"),

                 new Lines("interface A extends Comparator {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.EXTENDED_TYPE_CHANGED, new String[] { "Comparable", "Comparator" }, loc(1, 21), loc(1, 30), loc(1, 21), loc(1, 30)));
    }

    public void testInterfaceExtendsDeleted() {
        evaluate(new Lines("interface A extends Comparable {",
                           "}"),

                 new Lines("interface A {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.EXTENDED_TYPE_REMOVED, "Comparable", loc(1, 21), loc(1, 30), loc(1, 1), loc(2, 1)));
    }

    public void testClassImplementsAdded() {
        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A implements Runnable {",
                           "}"),

                 makeCodeChangedRef(Messages.IMPLEMENTED_TYPE_ADDED, "Runnable", loc(1, 1), loc(2, 1), loc(1, 20), loc(1, 27)));

        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A implements java.lang.Runnable {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.IMPLEMENTED_TYPE_ADDED, "java.lang.Runnable", loc(1, 1), loc(2, 1), loc(1, 20), loc(1, 37)));
    }

    public void testClassImplementsChanged() {
        // Thanks to Pat for finding and reporting this.
        evaluate(new Lines("class A implements Cloneable {",
                           "}"),

                 new Lines("class A implements Runnable {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.IMPLEMENTED_TYPE_CHANGED, new String[] { "Cloneable", "Runnable" }, loc(1, 20), loc(1, 28), loc(1, 20), loc(1, 27)));
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
                 
                 makeCodeChangedRef(Messages.IMPLEMENTED_TYPE_REMOVED, "Runnable", loc(1, 20), loc(1, 27), loc(1, 1), loc(2, 1)));

        evaluate(new Lines("class A implements java.lang.Runnable {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(Messages.IMPLEMENTED_TYPE_REMOVED, "java.lang.Runnable", loc(1, 20), loc(1, 37), loc(1, 1), loc(2, 1)));
    }
}
