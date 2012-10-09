package org.incava.diffj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.ParameterUtil;

public class Parameters {
    private final ASTFormalParameters params;
    
    public Parameters(ASTFormalParameters params) {
        this.params = params;
    }

    public void diff(ASTFormalParameters toParams, Differences differences) {
        List<String> fromParamTypes = ParameterUtil.getParameterTypes(params);
        List<String> toParamTypes = ParameterUtil.getParameterTypes(toParams);

        int fromSize = fromParamTypes.size();
        int toSize = toParamTypes.size();
        
        if (fromSize > 0) {
            if (toSize > 0) {
                compareEachParameter(toParams, fromSize, differences);
            }
            else {
                markParametersRemoved(toParams, differences);
            }
        }
        else if (toSize > 0) {
            markParametersAdded(toParams, differences);
        }
    }

    protected void markParametersAdded(ASTFormalParameters toFormalParams, Differences differences) {
        List<Token> names = ParameterUtil.getParameterNames(toFormalParams);
        for (Token name : names) {
            differences.changed(params, name, Messages.PARAMETER_ADDED, name.image);
        }
    }

    protected void markParametersRemoved(ASTFormalParameters toFormalParams, Differences differences) {
        List<Token> names = ParameterUtil.getParameterNames(params);
        for (Token name : names) {
            differences.changed(name, toFormalParams, Messages.PARAMETER_REMOVED, name.image);
        }
    }

    protected void markParameterTypeChanged(ASTFormalParameter fromParam, ASTFormalParameters toFormalParams, int idx, Differences differences) {
        ASTFormalParameter toParam = ParameterUtil.getParameter(toFormalParams, idx);
        String toType = ParameterUtil.getParameterType(toParam);
        differences.changed(fromParam, toParam, Messages.PARAMETER_TYPE_CHANGED, ParameterUtil.getParameterType(fromParam), toType);
    }

    protected void markParameterNameChanged(ASTFormalParameter fromParam, ASTFormalParameters toFormalParams, int idx, Differences differences) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        Token toNameTk = ParameterUtil.getParameterName(toFormalParams, idx);
        differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_NAME_CHANGED, fromNameTk.image, toNameTk.image);
    }

    protected void checkForReorder(ASTFormalParameter fromParam, int fromIdx, ASTFormalParameters toFormalParams, int toIdx, Differences differences) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        Token toNameTk = ParameterUtil.getParameterName(toFormalParams, toIdx);
        if (fromNameTk.image.equals(toNameTk.image)) {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
        }
        else {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED_AND_RENAMED, fromNameTk.image, fromIdx, toIdx, toNameTk.image);
        }
    }

    protected void markReordered(ASTFormalParameter fromParam, int fromIdx, ASTFormalParameters toParams, int toIdx, Differences differences) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        ASTFormalParameter toParam = ParameterUtil.getParameter(toParams, toIdx);
        differences.changed(fromParam, toParam, Messages.PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
    }

    protected void markRemoved(ASTFormalParameter fromParam, ASTFormalParameters toParams, Differences differences) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        differences.changed(fromParam, toParams, Messages.PARAMETER_REMOVED, fromNameTk.image);
    }

    /**
     * Compares each parameter. Assumes that the lists are the same size.
     */
    public void compareEachParameter(ASTFormalParameters toFormalParams, int size, Differences differences) {
        List<ASTFormalParameter> fromFormalParamList = ParameterUtil.getParameters(params);
        List<ASTFormalParameter> toFormalParamList = ParameterUtil.getParameters(toFormalParams);

        for (int idx = 0; idx < size; ++idx) {
            ASTFormalParameter fromFormalParam = fromFormalParamList.get(idx);
            Integer[] paramMatch = ParameterUtil.getMatch(fromFormalParamList, idx, toFormalParamList);

            if (paramMatch[0] == idx && paramMatch[1] == idx) {
                continue;
            }
            else if (paramMatch[0] == idx) {
                markParameterNameChanged(fromFormalParam, toFormalParams, idx, differences);
            }
            else if (paramMatch[1] == idx) {
                markParameterTypeChanged(fromFormalParam, toFormalParams, idx, differences);
            }
            else if (paramMatch[0] >= 0) {
                checkForReorder(fromFormalParam, idx, toFormalParams, paramMatch[0], differences);
            }
            else if (paramMatch[1] >= 0) {
                markReordered(fromFormalParam, idx, toFormalParams, paramMatch[1], differences);
            }
            else {
                markRemoved(fromFormalParam, toFormalParams, differences);
            }
        }

        Iterator<ASTFormalParameter> toIt = toFormalParamList.iterator();
        for (int toIdx = 0; toIt.hasNext(); ++toIdx) {
            ASTFormalParameter toParam = toIt.next();
            if (toParam != null) {
                ASTFormalParameter toFormalParam = ParameterUtil.getParameter(toFormalParams, toIdx);
                Token toName = ParameterUtil.getParameterName(toFormalParam);
                differences.changed(params, toFormalParam, Messages.PARAMETER_ADDED, toName.image);
            }
        }
    }
}
