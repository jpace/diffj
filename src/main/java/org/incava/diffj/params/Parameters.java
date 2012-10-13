package org.incava.diffj.params;

import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Differences;
import org.incava.diffj.Messages;
import org.incava.pmdx.ParameterUtil;

public class Parameters {
    private final ASTFormalParameters params;
    
    public Parameters(ASTFormalParameters params) {
        this.params = params;
    }

    public void diff(Parameters toParams, Differences differences) {
        List<String> fromParamTypes = getParameterTypes();
        List<String> toParamTypes = toParams.getParameterTypes();

        if (hasParameters()) {
            if (toParams.hasParameters()) {
                compareEachParameter(toParams, differences);
            }
            else {
                markParametersRemoved(toParams, differences);
            }
        }
        else if (toParams.hasParameters()) {
            markParametersAdded(toParams, differences);
        }
    }
    
    public ASTFormalParameters getFormalParameters() {
        return params;
    }

    protected boolean hasParameters() {
        return !getParameterTypes().isEmpty();
    }

    protected List<String> getParameterTypes() {
        return ParameterUtil.getParameterTypes(params);
    }

    protected List<Token> getParameterNames() {
        return ParameterUtil.getParameterNames(params);
    }

    protected List<ASTFormalParameter> getParameterList() {
        return ParameterUtil.getParameters(params);
    }

    protected ASTFormalParameter getParameter(int idx) {
        return ParameterUtil.getParameter(params, idx);
    }

    protected Token getParameterName(int idx) {
        return ParameterUtil.getParameterName(params, idx);
    }

    protected void markParametersAdded(Parameters toParams, Differences differences) {
        List<Token> names = toParams.getParameterNames();
        for (Token name : names) {
            differences.changed(params, name, Messages.PARAMETER_ADDED, name.image);
        }
    }

    protected void markParametersRemoved(Parameters toParams, Differences differences) {
        List<Token> names = getParameterNames();
        for (Token name : names) {
            differences.changed(name, toParams.params, Messages.PARAMETER_REMOVED, name.image);
        }
    }

    public void compareEachParameter(Parameters toParams, Differences differences) {
        ParameterComparator pc = new ParameterComparator(this, toParams);
        List<ParameterMatch> matches = pc.getMatches();
        tr.Ace.cyan("matches", matches);

        int size = matches.size();

        for (int idx = 0; idx < size; ++idx) {
            ParameterMatch paramMatch = matches.get(idx);
            tr.Ace.cyan("idx", idx);
            tr.Ace.cyan("paramMatch", paramMatch);
            paramMatch.diff(idx, toParams, differences);
        }

        tr.Ace.yellow("getUnmatchedToParameters()", pc.getUnmatchedToParameters());

        List<ASTFormalParameter> unmatchedParams = pc.getUnmatchedToParameters();
        for (ASTFormalParameter unmatchedParam : unmatchedParams) {
            tr.Ace.bold("unmatchedParam", unmatchedParam);
            if (unmatchedParam == null) {
                continue;
            }

            // ASTFormalParameter toFormalParam = toParams.getParameter(toIdx);
            // tr.Ace.bold("toFormalParam", toFormalParam);
            Parameter toParam = new Parameter(unmatchedParam);
            Token toName = toParam.getParameterName();
            tr.Ace.bold("toName", toName);
            differences.changed(params, unmatchedParam, Messages.PARAMETER_ADDED, toName.image);
        }
    }

    public double getMatchScore(Parameters toParams) {
        ParameterTypes fromTypes = new ParameterTypes(getParameterTypes());
        ParameterTypes toTypes = new ParameterTypes(toParams.getParameterTypes());
        return fromTypes.getMatchScore(toTypes);
    }
}
