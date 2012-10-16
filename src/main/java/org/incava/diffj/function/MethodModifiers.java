package org.incava.diffj.function;

import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.element.Modifiers;

public class MethodModifiers extends Modifiers {
    public static final int[] MODIFIERS = new int[] {
        JavaParserConstants.ABSTRACT,
        JavaParserConstants.FINAL,
        JavaParserConstants.NATIVE,
        JavaParserConstants.STATIC,
        JavaParserConstants.STRICTFP
    };

    public MethodModifiers(SimpleNode node) {
        super(node);
    }

    public int[] getModifierTypes() {
        return MODIFIERS;
    }
}
