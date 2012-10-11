package org.incava.diffj;

import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import org.incava.pmdx.SimpleNodeUtil;

public abstract class Function extends Item {
    private final SimpleNode node;
    
    public Function(SimpleNode node) {
        super(node);
        this.node = node;
    }

    protected void compareParameters(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams, Differences differences) {
        Parameters params = new Parameters(fromFormalParams);
        params.diff(toFormalParams, differences);
    }
    
    protected void compareThrows(Function toFunction, Differences differences) {
        Throws fromThrows = getThrows();
        Throws toThrows = toFunction.getThrows();
        fromThrows.diff(toThrows, differences);
    }

    protected Throws getThrows() {
        return new Throws(node, getThrowsList());
    }

    abstract protected ASTNameList getThrowsList();
}
