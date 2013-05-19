package org.incava.diffj.params;

import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;
import org.incava.ijdk.text.Message;
import org.incava.pmdx.ParameterUtil;

public class Parameters {
    public static final Message PARAMETER_REMOVED = new Message("parameter removed: {0}");
    public static final Message PARAMETER_ADDED = new Message("parameter added: {0}");
    public static final Message PARAMETER_REORDERED = new Message("parameter {0} reordered from argument {1} to {2}");
    public static final Message PARAMETER_TYPE_CHANGED = new Message("parameter type changed from {0} to {1}");
    public static final Message PARAMETER_NAME_CHANGED = new Message("parameter name changed from {0} to {1}");
    public static final Message PARAMETER_REORDERED_AND_RENAMED = new Message("parameter {0} reordered from argument {1} to {2} and renamed {3}");

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
            differences.changed(params, name, PARAMETER_ADDED, name.image);
        }
    }

    protected void markParametersRemoved(Parameters toParams, Differences differences) {
        List<Token> names = getParameterNames();
        for (Token name : names) {
            differences.changed(name, toParams.params, PARAMETER_REMOVED, name.image);
        }
    }

    public void compareEachParameter(Parameters toParams, Differences differences) {
        ParameterComparator pc = new ParameterComparator(this, toParams);
        List<ParameterMatch> matches = pc.getMatches();

        int size = matches.size();

        for (int idx = 0; idx < size; ++idx) {
            ParameterMatch paramMatch = matches.get(idx);
            paramMatch.diff(differences);
        }

        List<ASTFormalParameter> unmatchedParams = pc.getUnmatchedToParameters();
        for (ASTFormalParameter unmatchedParam : unmatchedParams) {
            if (unmatchedParam == null) {
                continue;
            }

            Parameter toParam = new Parameter(unmatchedParam);
            Token toName = toParam.getParameterName();
            differences.changed(params, unmatchedParam, PARAMETER_ADDED, toName.image);
        }
    }

    public double getMatchScore(Parameters toParams) {
        ParameterTypes fromTypes = new ParameterTypes(getParameterTypes());
        ParameterTypes toTypes = new ParameterTypes(toParams.getParameterTypes());
        return fromTypes.getMatchScore(toTypes);
    }
}
