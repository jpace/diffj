package org.incava.diffj.function;

import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserConstants;
import org.incava.diffj.element.Modifiers;

public class MethodModifiers extends Modifiers {
    public static final int[] MODIFIERS = new int[] {
        JavaParserConstants.ABSTRACT,
        JavaParserConstants.FINAL,
        JavaParserConstants.NATIVE,
        JavaParserConstants.STATIC,
        JavaParserConstants.STRICTFP
    };

    public MethodModifiers(AbstractJavaNode node) {
        super(node);
    }

    public int[] getModifierTypes() {
        return MODIFIERS;
    }
}
