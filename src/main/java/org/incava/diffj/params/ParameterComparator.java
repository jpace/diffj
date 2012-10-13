package org.incava.diffj.params;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;

public class ParameterComparator {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };
    
    private final List<ASTFormalParameter> fromFormalParamList;
    private final List<ASTFormalParameter> toFormalParamList;
    
    public ParameterComparator(List<ASTFormalParameter> fromFormalParamList, List<ASTFormalParameter> toFormalParamList) {
        this.fromFormalParamList = fromFormalParamList;
        this.toFormalParamList = toFormalParamList;
    }

    public List<ParameterMatch> getMatches() {
        List<ParameterMatch> matches = new ArrayList<ParameterMatch>();
        for (int idx = 0; idx < fromFormalParamList.size(); ++idx) {
            matches.add(getMatch(idx));
        }
        return matches;
    }

    public ParameterMatch getMatch(int fromIdx) {
        final ParameterMatch noMatch = new ParameterMatch(-1, -1);

        tr.Ace.setVerbose(true);

        ParameterMatch paramMatch = getParamMatch(fromIdx);
        tr.Ace.onRed("paramMatch", paramMatch);

        if (paramMatch.isExactMatch()) {
            clearFromLists(fromIdx, paramMatch.getNameMatch());
            return paramMatch;
        }

        Integer bestMatch = paramMatch.getFirstMatch();
        tr.Ace.onRed("bestMatch", bestMatch);
        
        if (bestMatch < 0) {
            return noMatch;
        }
        
        clearFromLists(fromIdx, bestMatch);
        return paramMatch;
    }

    private ParameterMatch getParamMatch(int fromIdx) {
        int typeMatch = -1;
        int nameMatch = -1;

        ASTFormalParameter fromFormalParam = fromFormalParamList.get(fromIdx);
        if (fromFormalParam == null) {
            return new ParameterMatch(typeMatch, nameMatch);
        }

        Parameter fromParam = new Parameter(fromFormalParam);

        for (int toIdx = 0; toIdx < toFormalParamList.size(); ++toIdx) {
            ASTFormalParameter toFormalParam = toFormalParamList.get(toIdx);
            if (toFormalParam == null) {
                continue;
            }

            Parameter toParam = new Parameter(toFormalParam);

            if (fromParam.isTypeEqual(toParam)) {
                typeMatch = toIdx;
            }

            if (fromParam.isNameEqual(toParam)) {
                nameMatch = toIdx;
            }

            if (typeMatch == toIdx && nameMatch == toIdx) {
                break;
            }
        }
        return new ParameterMatch(typeMatch, nameMatch);
    }

    private void clearFromLists(int fromIdx, int toIdx) {
        fromFormalParamList.set(fromIdx, null);
        toFormalParamList.set(toIdx, null);
    }
}
