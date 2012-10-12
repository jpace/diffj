package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.params.Parameters;
import org.incava.pmdx.SimpleNodeUtil;

public abstract class Function extends CodedElement {
    private final SimpleNode node;
    
    public Function(SimpleNode node) {
        super(node);
        this.node = node;
    }

    abstract protected ASTNameList getThrowsList();

    abstract protected ASTFormalParameters getFormalParameters();

    protected Parameters getParameters() {
        return new Parameters(getFormalParameters());
    }

    protected void compareParameters(Function toFunction, Differences differences) {
        Parameters fromParams = getParameters();
        Parameters toParams = toFunction.getParameters();
        fromParams.diff(toParams, differences);
    }
    
    protected void compareThrows(Function toFunction, Differences differences) {
        Throws fromThrows = getThrows();
        Throws toThrows = toFunction.getThrows();
        fromThrows.diff(toThrows, differences);
    }

    protected Throws getThrows() {
        return new Throws(node, getThrowsList());
    }

    protected Code getCode() {
        return new Code(getName(), getCodeTokens());
    }
}
