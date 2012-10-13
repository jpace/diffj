package org.incava.diffj.params;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
=======
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import org.incava.pmdx.ParameterUtil;
>>>>>>> 356c9a7fb142e4cc5f65aa1c9deeb00598e7eaa7

public class ParameterComparator {
    public enum StatusType { NAME_CHANGED, TYPE_CHANGED, REORDERED, REMOVED, ADDED };
    
    private final List<ASTFormalParameter> fromFormalParamList;
    private final List<ASTFormalParameter> unmatchedToParams;
    private final Parameters fromParams;
    private final Parameters toParams;
    
    public ParameterComparator(Parameters fromParams, Parameters toParams) {
        this.fromFormalParamList = new ArrayList<ASTFormalParameter>(fromParams.getParameterList());
        this.unmatchedToParams = new ArrayList<ASTFormalParameter>(toParams.getParameterList());
        this.fromParams = fromParams;
        this.toParams = toParams;
    }

    public List<ParameterMatch> getMatches() {
        List<ParameterMatch> matches = new ArrayList<ParameterMatch>();
        for (int idx = 0; idx < fromFormalParamList.size(); ++idx) {
            matches.add(getMatch(idx));
        }
        return matches;
    }

    public List<ASTFormalParameter> getUnmatchedToParameters() {
        return unmatchedToParams;
    }

    public ParameterMatch getMatch(int fromIdx) {
        ASTFormalParameter fromFormalParam = fromFormalParamList.get(fromIdx);        

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
            return new ParameterMatch(fromFormalParam, fromIdx, -1, -1);
        }
        
        clearFromLists(fromIdx, bestMatch);
        return paramMatch;
    }

    private ParameterMatch getParamMatch(int fromIdx) {
        int typeMatch = -1;
        int nameMatch = -1;

        ASTFormalParameter fromFormalParam = fromFormalParamList.get(fromIdx);
        if (fromFormalParam == null) {
            return new ParameterMatch(null, fromIdx, -1, -1);
        }

        Parameter fromParam = new Parameter(fromFormalParam);

        for (int toIdx = 0; toIdx < unmatchedToParams.size(); ++toIdx) {
            ASTFormalParameter toFormalParam = unmatchedToParams.get(toIdx);
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
        return new ParameterMatch(fromFormalParam, fromIdx, typeMatch, nameMatch);
    }

    private void clearFromLists(int fromIdx, int toIdx) {
        fromFormalParamList.set(fromIdx, null);
        unmatchedToParams.set(toIdx, null);
    }
}
