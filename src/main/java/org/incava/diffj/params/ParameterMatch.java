package org.incava.diffj.params;

import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Differences;
import org.incava.diffj.Messages;

public class ParameterMatch {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };

    private final ASTFormalParameter fromFormalParam;
    private final int typeMatch;
    private final int nameMatch;

    public ParameterMatch(ASTFormalParameter fromFormalParam, int typeMatch, int nameMatch) {
        this.fromFormalParam = fromFormalParam;
        this.typeMatch = typeMatch;
        this.nameMatch = nameMatch;
    }

    public int getTypeMatch() {
        return typeMatch;
    }

    public int getNameMatch() {
        return nameMatch;
    }
    
    public boolean isMatch(int idx) {
        return typeMatch == idx && nameMatch == idx;
    }

    public boolean isTypeMatch(int idx) {
        return typeMatch == idx;
    }

    public boolean isNameMatch(int idx) {
        return nameMatch == idx;
    }    

    public boolean hasTypeMatch() {
        return typeMatch >= 0;
    }

    public boolean hasNameMatch() {
        return nameMatch >= 0;
    }

    public boolean isExactMatch() {
        int typeMatch = getTypeMatch();
        return typeMatch >= 0 && getTypeMatch() == getNameMatch();
    }

    public int getFirstMatch() {
        int typeMatch = getTypeMatch();
        return typeMatch >= 0 ? typeMatch : getNameMatch();
    }

    public String toString() {
        return String.valueOf(typeMatch) + ", " + nameMatch;
    }

    public void diff(int idx, Parameters toParams, Differences differences) {
        if (isMatch(idx)) {
            return;
        }
        else if (isTypeMatch(idx)) {
            markParameterNameChanged(idx, toParams, differences);
        }
        else if (isNameMatch(idx)) {
            markParameterTypeChanged(idx, toParams, differences);
        }
        else if (hasTypeMatch()) {
            tr.Ace.onBlue("getTypeMatch()", getTypeMatch());
            checkForReorder(idx, toParams, differences);
        }
        else if (hasNameMatch()) {
            markReordered(idx, toParams, differences);
        }
        else {
            markRemoved(idx, toParams, differences);
        }
    }

    public void markReordered(int fromIdx, Parameters toParams, Differences differences) {
        tr.Ace.bold("-------------------------------------------------------");
        tr.Ace.bold("fromIdx", fromIdx);
        tr.Ace.bold("typeMatch", typeMatch);
        tr.Ace.bold("nameMatch", nameMatch);
        int toIdx = getNameMatch();
        tr.Ace.bold("toIdx", toIdx);
        
        Parameter fromParam = new Parameter(fromFormalParam);
        Token fromNameTk = fromParam.getParameterName();
        ASTFormalParameter toParam = toParams.getParameter(toIdx);
        differences.changed(fromFormalParam, toParam, Messages.PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
    }

    public void checkForReorder(int fromIdx, Parameters toParams, Differences differences) {
        int toIdx = getTypeMatch();
        Parameter fromParam = new Parameter(fromFormalParam);
        Token fromNameTk = fromParam.getParameterName();
        Token toNameTk = toParams.getParameterName(toIdx);
        if (fromNameTk.image.equals(toNameTk.image)) {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
        }
        else {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED_AND_RENAMED, fromNameTk.image, fromIdx, toIdx, toNameTk.image);
        }
    }

    public void markRemoved(int idx, Parameters toParams, Differences differences) {
        Parameter fromParam = new Parameter(fromFormalParam);
        Token fromNameTk = fromParam.getParameterName();
        differences.changed(fromFormalParam, toParams.getFormalParameters(), Messages.PARAMETER_REMOVED, fromNameTk.image);
    }

    public void markParameterTypeChanged(int idx, Parameters toParams, Differences differences) {
        Parameter fromParam = new Parameter(fromFormalParam);
        ASTFormalParameter toFormalParam = toParams.getParameter(idx);
        Parameter toParam = new Parameter(toFormalParam);
        String fromType = fromParam.getParameterType();
        String toType = toParam.getParameterType();
        differences.changed(fromFormalParam, toFormalParam, Messages.PARAMETER_TYPE_CHANGED, fromType, toType);
    }

    public void markParameterNameChanged(int idx, Parameters toParams, Differences differences) {
        Parameter fromParam = new Parameter(fromFormalParam);
        Token fromNameTk = fromParam.getParameterName();
        Token toNameTk = toParams.getParameterName(idx);
        differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_NAME_CHANGED, fromNameTk.image, toNameTk.image);
    }
}
