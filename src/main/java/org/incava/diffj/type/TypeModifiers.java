package org.incava.diffj.type;

import net.sourceforge.pmd.ast.JavaParserConstants;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.element.Modifiers;

public class TypeModifiers extends Modifiers {
    public static final int[] MODIFIERS = new int[] {
        JavaParserConstants.ABSTRACT,
        JavaParserConstants.FINAL,
        JavaParserConstants.STATIC, // valid only for inner types
        JavaParserConstants.STRICTFP
    };

    public TypeModifiers(SimpleNode node) {
        super(node);
    }

    public int[] getModifierTypes() {
        return MODIFIERS;
    }
}
