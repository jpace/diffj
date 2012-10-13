package org.incava.diffj.params;

import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Differences;
import org.incava.diffj.Messages;

public class ParameterMatch {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };

    public static ParameterMatch create(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        if (typeMatch == index) {
            if (nameMatch == index) {
                return new ParameterExactMatch(fromFormalParam, index, typeMatch, nameMatch, toParams);
            }
            else {
                return new ParameterExactTypeMatch(fromFormalParam, index, typeMatch, nameMatch, toParams);
            }
        }
        else if (nameMatch == index) {
            return new ParameterExactNameMatch(fromFormalParam, index, typeMatch, nameMatch, toParams);
        }
        else if (typeMatch >= 0) {
            return new ParameterTypeMatch(fromFormalParam, index, typeMatch, nameMatch, toParams);
        }
        else if (nameMatch >= 0) {
            return new ParameterNameMatch(fromFormalParam, index, typeMatch, nameMatch, toParams);
        }
        return new ParameterMatch(fromFormalParam, index, typeMatch, nameMatch, toParams);
    }

    protected final ASTFormalParameter fromFormalParam;
    protected final Parameter fromParam;
    protected final int index;
    protected final int typeMatch;
    protected final int nameMatch;
    protected final Parameters toParams;

    public ParameterMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        this.fromFormalParam = fromFormalParam;
        this.fromParam = new Parameter(fromFormalParam);
        this.index = index;
        this.typeMatch = typeMatch;
        this.nameMatch = nameMatch;
        this.toParams = toParams;
    }

    public int getTypeMatch() {
        return typeMatch;
    }

    public int getNameMatch() {
        return nameMatch;
    }
    
    public boolean isMatch() {
        return typeMatch == index && nameMatch == index;
    }

    public boolean isTypeMatch() {
        return typeMatch == index;
    }

    public boolean isNameMatch() {
        return nameMatch == index;
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

    public void diff(Differences differences) {
        if (isMatch()) {
            return;
        }
        else if (isTypeMatch()) {
            markParameterNameChanged(differences);
        }
        else if (isNameMatch()) {
            markParameterTypeChanged(differences);
        }
        else if (hasTypeMatch()) {
            checkForReorder(differences);
        }
        else if (hasNameMatch()) {
            markReordered(differences);
        }
        else {
            markRemoved(differences);
        }
    }

    protected Token getParameterName() {
        return fromParam.getParameterName();
    }

    public void markReordered(Differences differences) {
        int toIdx = getNameMatch();
        Token fromNameTk = getParameterName();
        ASTFormalParameter toParam = toParams.getParameter(toIdx);
        differences.changed(fromFormalParam, toParam, Messages.PARAMETER_REORDERED, fromNameTk.image, index, toIdx);
    }

    public void checkForReorder(Differences differences) {
        int toIdx = getTypeMatch();
        Token fromNameTk = getParameterName();
        Token toNameTk = toParams.getParameterName(toIdx);
        if (fromNameTk.image.equals(toNameTk.image)) {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED, fromNameTk.image, index, toIdx);
        }
        else {
            differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_REORDERED_AND_RENAMED, fromNameTk.image, index, toIdx, toNameTk.image);
        }
    }

    public void markRemoved(Differences differences) {
        Token fromNameTk = getParameterName();
        differences.changed(fromFormalParam, toParams.getFormalParameters(), Messages.PARAMETER_REMOVED, fromNameTk.image);
    }

    public void markParameterTypeChanged(Differences differences) {
        ASTFormalParameter toFormalParam = toParams.getParameter(index);
        Parameter toParam = new Parameter(toFormalParam);
        String fromType = fromParam.getParameterType();
        String toType = toParam.getParameterType();
        differences.changed(fromFormalParam, toFormalParam, Messages.PARAMETER_TYPE_CHANGED, fromType, toType);
    }

    public void markParameterNameChanged(Differences differences) {
        Token fromNameTk = getParameterName();
        Token toNameTk = toParams.getParameterName(index);
        differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_NAME_CHANGED, fromNameTk.image, toNameTk.image);
    }
}
