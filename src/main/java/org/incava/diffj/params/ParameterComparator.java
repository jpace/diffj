package org.incava.diffj.params;

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

    public ParameterMatch toMatch(Integer[] score) {
        return new ParameterMatch(score);
    }

    public ParameterMatch getMatch(int fromIdx) {
        final ParameterMatch noMatch = new ParameterMatch(-1, -1);

        Integer[] typeAndNameMatch = getParamMatch(fromIdx);
        if (typeAndNameMatch[0] >= 0 && typeAndNameMatch[0] == typeAndNameMatch[1]) {
            clearFromLists(fromIdx, typeAndNameMatch[1]);
            return toMatch(typeAndNameMatch);
        }

        Integer bestMatch = typeAndNameMatch[0] >= 0 ? typeAndNameMatch[0] : typeAndNameMatch[1];
        
        if (bestMatch < 0) {
            return noMatch;
        }

        // make sure there isn't an exact match for this somewhere else in
        // fromParameters
        ASTFormalParameter to = toFormalParamList.get(bestMatch);

        int fromMatch = getExactMatch(to);

        if (fromMatch >= 0) {
            return noMatch;
        }
        
        clearFromLists(fromIdx, bestMatch);
        return toMatch(typeAndNameMatch);
    }

    private Integer[] getParamMatch(int fromIdx) {
        int typeMatch = -1;
        int nameMatch = -1;

        Integer[] typeAndNameMatch = new Integer[] { -1, -1 };
        ASTFormalParameter fromParam = fromFormalParamList.get(fromIdx);

        for (int toIdx = 0; toIdx < toFormalParamList.size(); ++toIdx) {
            ASTFormalParameter toParam = toFormalParamList.get(toIdx);

            if (toParam == null) {
                continue;
            }

            if (areTypesEqual(fromParam, toParam)) {
                typeAndNameMatch[0] = toIdx;
                typeMatch = toIdx;
            }

            if (areNamesEqual(fromParam, toParam)) {
                typeAndNameMatch[1] = toIdx;
                nameMatch = toIdx;
            }

            if (typeMatch == toIdx && nameMatch == toIdx) {
                return typeAndNameMatch;
            }
        }
        return typeAndNameMatch;
    }

    private int getExactMatch(ASTFormalParameter toFormalParam) {
        Parameter toParam = new Parameter(toFormalParam);

        int idx = 0;
        for (ASTFormalParameter from : fromFormalParamList) {
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

    private boolean areTypesEqual(ASTFormalParameter fromFormalParam, ASTFormalParameter toFormalParam) {
        if (fromFormalParam == null) {
            return false;
        }
        Parameter fromParam = new Parameter(fromFormalParam);
        Parameter toParam = new Parameter(toFormalParam);
        return fromParam.isTypeEqual(toParam);
    }

    private boolean areNamesEqual(ASTFormalParameter fromFormalParam, ASTFormalParameter toFormalParam) {
        if (fromFormalParam == null) {
            return false;
        }
        Parameter fromParam = new Parameter(fromFormalParam);
        Parameter toParam = new Parameter(toFormalParam);
        return fromParam.isNameEqual(toParam);
    }

    private void clearFromLists(int fromIdx, int toIdx) {
        fromFormalParamList.set(fromIdx, null);
        toFormalParamList.set(toIdx, null);
    }
}
