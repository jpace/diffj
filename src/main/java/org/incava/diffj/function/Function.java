package org.incava.diffj.function;

import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.diffj.code.Code;
import org.incava.diffj.element.CodedElement;
import org.incava.diffj.element.Differences;
import org.incava.diffj.params.Parameters;
import org.incava.pmdx.FunctionUtil;

public abstract class Function extends CodedElement {
    private final SimpleNode node;
    
    public Function(SimpleNode node) {
        super(node);
        this.node = node;
    }

    protected ASTNameList getThrowsList() {
        return FunctionUtil.getThrowsList(node);
    }

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
}
