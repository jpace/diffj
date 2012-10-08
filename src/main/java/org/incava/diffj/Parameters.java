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
    private final DiffComparator differences;
    private final ASTFormalParameters fromFormalParams;
    private final ASTFormalParameters toFormalParams;
    
    public Parameters(FileDiffs fileDiffs, ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams) {
        this.differences = new DiffComparator(fileDiffs);
        this.fromFormalParams = fromFormalParams;
        this.toFormalParams = toFormalParams;
    }

    public void compare() {
        List<String> fromParamTypes = ParameterUtil.getParameterTypes(fromFormalParams);
        List<String> toParamTypes = ParameterUtil.getParameterTypes(toFormalParams);

        int fromSize = fromParamTypes.size();
        int toSize = toParamTypes.size();
        
        if (fromSize > 0) {
            if (toSize > 0) {
                compareEachParameter(fromFormalParams, toFormalParams, fromSize);
            }
            else {
                markParametersRemoved(fromFormalParams, toFormalParams);
            }
        }
        else if (toSize > 0) {
            markParametersAdded(fromFormalParams, toFormalParams);
        }
    }

    protected void markParametersAdded(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams) {
        List<Token> names = ParameterUtil.getParameterNames(toFormalParams);
        for (Token name : names) {
            differences.changed(fromFormalParams, name, Messages.PARAMETER_ADDED, name.image);
        }
    }

    protected void markParametersRemoved(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams) {
        List<Token> names = ParameterUtil.getParameterNames(fromFormalParams);
        for (Token name : names) {
            differences.changed(name, toFormalParams, Messages.PARAMETER_REMOVED, name.image);
        }
    }

    protected void markParameterTypeChanged(ASTFormalParameter fromParam, ASTFormalParameters toFormalParams, int idx) {
        ASTFormalParameter toParam = ParameterUtil.getParameter(toFormalParams, idx);
        String toType = ParameterUtil.getParameterType(toParam);
        differences.changed(fromParam, toParam, Messages.PARAMETER_TYPE_CHANGED, ParameterUtil.getParameterType(fromParam), toType);
    }

    protected void markParameterNameChanged(ASTFormalParameter fromParam, ASTFormalParameters toFormalParams, int idx) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        Token toNameTk = ParameterUtil.getParameterName(toFormalParams, idx);
        differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_NAME_CHANGED, fromNameTk.image, toNameTk.image);
    }

    protected void checkForReorder(ASTFormalParameter fromParam, int fromIdx, ASTFormalParameters toFormalParams, int toIdx) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        Token toNameTk = ParameterUtil.getParameterName(toFormalParams, toIdx);
        if (fromNameTk.image.equals(toNameTk.image)) {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
        }
        else {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED_AND_RENAMED, fromNameTk.image, fromIdx, toIdx, toNameTk.image);
        }
    }

    protected void markReordered(ASTFormalParameter fromParam, int fromIdx, ASTFormalParameters toParams, int toIdx) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        ASTFormalParameter toParam = ParameterUtil.getParameter(toParams, toIdx);
        differences.changed(fromParam, toParam, Messages.PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
    }

    protected void markRemoved(ASTFormalParameter fromParam, ASTFormalParameters toParams) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        differences.changed(fromParam, toParams, Messages.PARAMETER_REMOVED, fromNameTk.image);
    }

    /**
     * Compares each parameter. Assumes that the lists are the same size.
     */
    public void compareEachParameter(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams, int size) {
        List<ASTFormalParameter> fromFormalParamList = ParameterUtil.getParameters(fromFormalParams);
        List<ASTFormalParameter> toFormalParamList = ParameterUtil.getParameters(toFormalParams);

        for (int idx = 0; idx < size; ++idx) {
            ASTFormalParameter fromFormalParam = fromFormalParamList.get(idx);
            Integer[] paramMatch = ParameterUtil.getMatch(fromFormalParamList, idx, toFormalParamList);

            if (paramMatch[0] == idx && paramMatch[1] == idx) {
                continue;
            }
            else if (paramMatch[0] == idx) {
                markParameterNameChanged(fromFormalParam, toFormalParams, idx);
            }
            else if (paramMatch[1] == idx) {
                markParameterTypeChanged(fromFormalParam, toFormalParams, idx);
            }
            else if (paramMatch[0] >= 0) {
                checkForReorder(fromFormalParam, idx, toFormalParams, paramMatch[0]);
            }
            else if (paramMatch[1] >= 0) {
                markReordered(fromFormalParam, idx, toFormalParams, paramMatch[1]);
            }
            else {
                markRemoved(fromFormalParam, toFormalParams);
            }
        }

        Iterator<ASTFormalParameter> toIt = toFormalParamList.iterator();
        for (int toIdx = 0; toIt.hasNext(); ++toIdx) {
            ASTFormalParameter toParam = toIt.next();
            if (toParam != null) {
                ASTFormalParameter toFormalParam = ParameterUtil.getParameter(toFormalParams, toIdx);
                Token toName = ParameterUtil.getParameterName(toFormalParam);
                differences.changed(fromFormalParams, toFormalParam, Messages.PARAMETER_ADDED, toName.image);
            }
        }
    }
}
