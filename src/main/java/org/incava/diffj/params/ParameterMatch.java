package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.element.Differences;

public abstract class ParameterMatch {
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
        return new ParameterNoMatch(fromFormalParam, index, typeMatch, nameMatch, toParams);
    }

    protected final ASTFormalParameter fromFormalParam;
    protected final Parameter fromParam;
    protected final int index;
    private final int typeMatch;
    private final int nameMatch;
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

    public abstract void diff(Differences differences);

    protected Token getParameterName() {
        return fromParam.getParameterName();
    }
}
