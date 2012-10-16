package org.incava.diffj.element;

import java.util.List;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.code.Code;

public abstract class CodedElement extends AccessibleElement {
    public CodedElement(SimpleNode node) {
        super(node);
    }

    abstract protected List<Token> getCodeTokens();

    abstract protected String getName();

    protected Code getCode() {
        return new Code(getName(), getCodeTokens());
    }

    public void compareCode(Code fromCode, Code toCode, Differences differences) {
        fromCode.diff(toCode, differences);
    }

    public void compareCode(CodedElement toCodedElement, Differences differences) {
        Code fromCode = getCode();
        Code toCode = toCodedElement.getCode();
        fromCode.diff(toCode, differences);
    }
}
