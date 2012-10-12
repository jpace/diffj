package org.incava.diffj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.ParameterUtil;

public class ParameterComparator {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };
    
    private final List<ASTFormalParameter> fromFormalParamList;
    private final List<ASTFormalParameter> toFormalParamList;
    
    public ParameterComparator(List<ASTFormalParameter> fromFormalParamList, List<ASTFormalParameter> toFormalParamList) {
        this.fromFormalParamList = fromFormalParamList;
        this.toFormalParamList = toFormalParamList;
    }

    public ParameterComparator() {
        this(null, null);
    }

    public Integer[] getParamMatches(List<ASTFormalParameter> fromFormalParams, int fromIdx, List<ASTFormalParameter> toFormalParams) {
        Integer[] typeAndNameMatch = new Integer[] { -1, -1 };
        ASTFormalParameter fromParam = fromFormalParams.get(fromIdx);

        for (int toIdx = 0; toIdx < toFormalParams.size(); ++toIdx) {
            ASTFormalParameter toParam = toFormalParams.get(toIdx);

            if (toParam == null) {
                continue;
            }

            if (areTypesEqual(fromParam, toParam)) {
                typeAndNameMatch[0] = toIdx;
            }

            if (areNamesEqual(fromParam, toParam)) {
                typeAndNameMatch[1] = toIdx;
            }

            if (typeAndNameMatch[0] == toIdx && typeAndNameMatch[1] == toIdx) {
                return typeAndNameMatch;
            }
        }
        return typeAndNameMatch;
    }

    public Integer[] getMatch(List<ASTFormalParameter> fromFormalParams, int fromIdx, List<ASTFormalParameter> toFormalParams) {
        final Integer[] noMatch = new Integer[] { -1, -1 };

        Integer[] typeAndNameMatch = getParamMatches(fromFormalParams, fromIdx, toFormalParams);
        if (typeAndNameMatch[0] >= 0 && typeAndNameMatch[0] == typeAndNameMatch[1]) {
            clearFromLists(fromFormalParams, fromIdx, toFormalParams, typeAndNameMatch[1]);
            return typeAndNameMatch;
        }

        Integer bestMatch = typeAndNameMatch[0] >= 0 ? typeAndNameMatch[0] : typeAndNameMatch[1];
        
        if (bestMatch < 0) {
            return noMatch;
        }

        // make sure there isn't an exact match for this somewhere else in
        // fromParameters
        ASTFormalParameter to = toFormalParams.get(bestMatch);

        int fromMatch = getExactMatch(fromFormalParams, to);

        if (fromMatch >= 0) {
            return noMatch;
        }
        
        clearFromLists(fromFormalParams, fromIdx, toFormalParams, bestMatch);
        return typeAndNameMatch;
    }

    public int getExactMatch(List<ASTFormalParameter> fromParamList, ASTFormalParameter toFormalParam) {
        Parameter toParam = new Parameter(toFormalParam);

        int idx = 0;
        for (ASTFormalParameter from : fromParamList) {
            if (from != null) {
                Parameter fromParam = new Parameter(from);
                if (fromParam.isTypeEqual(toParam) && fromParam.isNameEqual(toParam)) {
                    return idx;
                }
            }
            ++idx;
        }
        return -1;
    }

    public boolean areTypesEqual(ASTFormalParameter fromFormalParam, ASTFormalParameter toFormalParam) {
        if (fromFormalParam == null) {
            return false;
        }
        Parameter fromParam = new Parameter(fromFormalParam);
        Parameter toParam = new Parameter(toFormalParam);
        return fromParam.isTypeEqual(toParam);
    }

    public boolean areNamesEqual(ASTFormalParameter fromFormalParam, ASTFormalParameter toFormalParam) {
        if (fromFormalParam == null) {
            return false;
        }
        Parameter fromParam = new Parameter(fromFormalParam);
        Parameter toParam = new Parameter(toFormalParam);
        return fromParam.isNameEqual(toParam);
    }

    protected void clearFromLists(List<ASTFormalParameter> fromParameters, int fromIdx, List<ASTFormalParameter> toParameters, int toIdx) {
        fromParameters.set(fromIdx, null);
        toParameters.set(toIdx, null);
    }
}
