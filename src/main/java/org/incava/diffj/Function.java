package org.incava.diffj;

import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.SimpleNodeUtil;

public abstract class Function extends Item {
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

    protected void compareParameters(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams, Differences differences) {
        Parameters fromParams = getParameters();
        Parameters toParams = new Parameters(toFormalParams);
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

    abstract protected List<Token> getCodeTokens();

    abstract protected String getName();

    protected void compareBlocks(Function toFunction, Differences differences) {
        String fromName = getName();
        List<Token> fromTokens = getCodeTokens();
        List<Token> toTokens = toFunction.getCodeTokens();
        compareCode(fromName, fromTokens, toTokens, differences);
    }
}
