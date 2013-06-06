package org.incava.diffj.type;

import org.incava.analysis.FileDiffChange;
import org.incava.diffj.ItemsTest;
import org.incava.diffj.Lines;
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

                 makeAccessRef(locrg(1, 1, 5), locrg(1, 1, 6), null, "public"));
        
        evaluate(new Lines("public class Test {",
                           "}"),

                 new Lines("class Test {",
                           "}"),
                 
                 makeAccessRef(locrg(1, 1, 6), locrg(1, 1, 5), "public", null));
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
                 
                 makeInterfaceAddedRef(locrg(1, 1, 3, 1), locrg(3, 5, 22), "ITest"));
    }

    public void testClassInnerInterfaceRemoved() {
        evaluate(new Lines("class Test {",
                           "",
                           "    interface ITest {}",
                           "}"),

                 new Lines("class Test {",
                           "",
                           "}"),
                 
                 makeInterfaceRemovedRef(locrg(3, 5, 22), locrg(1, 1, 3, 1), "ITest"));
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
                 
                 makeCodeChangedRef(EXTENDED_TYPE_ADDED, "Date", locrg(1, 1, 2, 1), locrg(1, 17, 1, 20)));

        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A extends java.util.Date {",
                           "}"),
                 
                 makeCodeChangedRef(EXTENDED_TYPE_ADDED, "java.util.Date", locrg(1, 1, 2, 1), locrg(1, 17, 1, 30)));
    }

    public void testClassExtendsChanged() {
        // Thanks to Pat for finding and reporting this.
        evaluate(new Lines("class A extends Object {",
                           "}"),

                 new Lines("class A extends Date {",
                           "}"),
                 
                 makeCodeChangedRef(EXTENDED_TYPE_CHANGED, new String[] { "Object", "Date" }, locrg(1, 17, 22), locrg(1, 17, 20)));
    }

    public void testClassExtendsDeleted() {
        evaluate(new Lines("class A extends Date {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(EXTENDED_TYPE_REMOVED, "Date", locrg(1, 17, 20), locrg(1, 1, 2, 1)));

        evaluate(new Lines("class A extends java.util.Date {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(EXTENDED_TYPE_REMOVED, "java.util.Date", locrg(1, 17, 30), locrg(1, 1, 2, 1)));
    }

    public void testInterfaceExtendsAdded() {
        evaluate(new Lines("interface A {",
                           "}"),

                 new Lines("interface A extends Comparator {",
                           "}"),
                 
                 makeCodeChangedRef(EXTENDED_TYPE_ADDED, "Comparator", locrg(1, 1, 2, 1), locrg(1, 21, 30)));
    }

    public void testInterfaceExtendsChanged() {
        evaluate(new Lines("interface A extends Comparable {",
                           "}"),

                 new Lines("interface A extends Comparator {",
                           "}"),
                 
                 makeCodeChangedRef(EXTENDED_TYPE_CHANGED, new String[] { "Comparable", "Comparator" }, locrg(1, 21, 30), locrg(1, 21, 30)));
    }

    public void testInterfaceExtendsDeleted() {
        evaluate(new Lines("interface A extends Comparable {",
                           "}"),

                 new Lines("interface A {",
                           "}"),
                 
                 makeCodeChangedRef(EXTENDED_TYPE_REMOVED, "Comparable", locrg(1, 21, 30), locrg(1, 1, 2, 1)));
    }

    public void testClassImplementsAdded() {
        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A implements Runnable {",
                           "}"),

                 makeCodeChangedRef(IMPLEMENTED_TYPE_ADDED, "Runnable", locrg(1, 1, 2, 1), locrg(1, 20, 27)));

        evaluate(new Lines("class A {",
                           "}"),

                 new Lines("class A implements java.lang.Runnable {",
                           "}"),
                 
                 makeCodeChangedRef(IMPLEMENTED_TYPE_ADDED, "java.lang.Runnable", locrg(1, 1, 2, 1), locrg(1, 20, 37)));
    }

    public void testClassImplementsChanged() {
        // Thanks to Pat for finding and reporting this.
        evaluate(new Lines("class A implements Cloneable {",
                           "}"),

                 new Lines("class A implements Runnable {",
                           "}"),
                 
                 makeCodeChangedRef(IMPLEMENTED_TYPE_CHANGED, new String[] { "Cloneable", "Runnable" }, locrg(1, 20, 28), locrg(1, 20, 27)));
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
                 
                 makeCodeChangedRef(IMPLEMENTED_TYPE_REMOVED, "Runnable", locrg(1, 20, 27), locrg(1, 1, 2, 1)));

        evaluate(new Lines("class A implements java.lang.Runnable {",
                           "}"),

                 new Lines("class A {",
                           "}"),
                 
                 makeCodeChangedRef(IMPLEMENTED_TYPE_REMOVED, "java.lang.Runnable", locrg(1, 20, 37), locrg(1, 1, 2, 1)));
    }
}
