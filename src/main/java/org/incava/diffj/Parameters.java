package org.incava.diffj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.Token;
import org.incava.pmdx.ParameterUtil;

public class Parameters {
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
            differences.changed(params, name, Messages.PARAMETER_ADDED, name.image);
        }
    }

    protected void markParametersRemoved(Parameters toParams, Differences differences) {
        List<Token> names = getParameterNames();
        for (Token name : names) {
            differences.changed(name, toParams.params, Messages.PARAMETER_REMOVED, name.image);
        }
    }

    protected void markParameterTypeChanged(ASTFormalParameter fromFormalParam, Parameters toParams, int idx, Differences differences) {
        Parameter fromParam = new Parameter(fromFormalParam);
        ASTFormalParameter toFormalParam = toParams.getParameter(idx);
        Parameter toParam = new Parameter(toFormalParam);
        String fromType = fromParam.getParameterType();
        String toType = toParam.getParameterType();
        differences.changed(fromFormalParam, toFormalParam, Messages.PARAMETER_TYPE_CHANGED, fromType, toType);
    }

    protected void markParameterNameChanged(ASTFormalParameter fromFormalParam, Parameters toParams, int idx, Differences differences) {
        Parameter fromParam = new Parameter(fromFormalParam);
        Token fromNameTk = fromParam.getParameterName();
        Token toNameTk = toParams.getParameterName(idx);
        differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_NAME_CHANGED, fromNameTk.image, toNameTk.image);
    }

    protected void checkForReorder(ASTFormalParameter fromFormalParam, int fromIdx, Parameters toParams, int toIdx, Differences differences) {
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

    protected void markReordered(ASTFormalParameter fromFormalParam, int fromIdx, Parameters toParams, int toIdx, Differences differences) {
        Parameter fromParam = new Parameter(fromFormalParam);
        Token fromNameTk = fromParam.getParameterName();
        ASTFormalParameter toParam = toParams.getParameter(toIdx);
        differences.changed(fromFormalParam, toParam, Messages.PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
    }

    protected void markRemoved(ASTFormalParameter fromFormalParam, Parameters toParams, Differences differences) {
        Parameter fromParam = new Parameter(fromFormalParam);
        Token fromNameTk = fromParam.getParameterName();
        differences.changed(fromFormalParam, toParams.params, Messages.PARAMETER_REMOVED, fromNameTk.image);
    }

    public void compareEachParameter(Parameters toParams, Differences differences) {
        List<ASTFormalParameter> fromFormalParamList = getParameterList();
        List<ASTFormalParameter> toFormalParamList = toParams.getParameterList();

        int size = fromFormalParamList.size();

        for (int idx = 0; idx < size; ++idx) {
            ASTFormalParameter fromFormalParam = fromFormalParamList.get(idx);
            Integer[] paramMatch = ParameterUtil.getMatch(fromFormalParamList, idx, toFormalParamList);

            if (paramMatch[0] == idx && paramMatch[1] == idx) {
                continue;
            }
            else if (paramMatch[0] == idx) {
                markParameterNameChanged(fromFormalParam, toParams, idx, differences);
            }
            else if (paramMatch[1] == idx) {
                markParameterTypeChanged(fromFormalParam, toParams, idx, differences);
            }
            else if (paramMatch[0] >= 0) {
                checkForReorder(fromFormalParam, idx, toParams, paramMatch[0], differences);
            }
            else if (paramMatch[1] >= 0) {
                markReordered(fromFormalParam, idx, toParams, paramMatch[1], differences);
            }
            else {
                markRemoved(fromFormalParam, toParams, differences);
            }
        }

        Iterator<ASTFormalParameter> toIt = toFormalParamList.iterator();
        for (int toIdx = 0; toIt.hasNext(); ++toIdx) {
            ASTFormalParameter toParm = toIt.next();
            if (toParm == null) {
                continue;
            }

            ASTFormalParameter toFormalParam = toParams.getParameter(toIdx);
            Parameter toParam = new Parameter(toFormalParam);
            Token toName = toParam.getParameterName();
            differences.changed(params, toFormalParam, Messages.PARAMETER_ADDED, toName.image);
        }
    }

    protected void clearFromLists(List<ASTFormalParameter> fromParameters, int fromIdx, List<ASTFormalParameter> toParameters, int toIdx) {
        fromParameters.set(fromIdx, null);
        toParameters.set(toIdx, null);
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

    public double getMatchScore(Parameters to) {
        return getMatchScore(params, to.params);
    }

    public double getMatchScore(ASTFormalParameters from, ASTFormalParameters to) {
        if (from.jjtGetNumChildren() == 0 && to.jjtGetNumChildren() == 0) {
            return 1.0;
        }
        
        // (int[], double, String) <=> (int[], double, String) ==> 100% (3 of 3)
        // (int[], double, String) <=> (double, int[], String) ==> 80% (3 of 3 - 10% * misordered)
        // (int[], double)         <=> (double, int[], String) ==> 46% (2 of 3 - 10% * misordered)
        // (int[], double, String) <=> (String) ==> 33% (1 of 3 params)
        // (int[], double) <=> (String) ==> 0 (0 of 3)

        Parameters fromParams = new Parameters(from);
        Parameters toParams = new Parameters(to);

        List<String> fromParamTypes = fromParams.getParameterTypes();
        List<String> toParamTypes = toParams.getParameterTypes();

        int fromSize = fromParamTypes.size();
        int toSize = toParamTypes.size();

        int exactMatches = 0;
        int misorderedMatches = 0;
            
        for (int fromIdx = 0; fromIdx < fromSize; ++fromIdx) {
            int paramMatch = getListMatch(fromParamTypes, fromIdx, toParamTypes);
            if (paramMatch == fromIdx) {
                ++exactMatches;
            }
            else if (paramMatch >= 0) {
                ++misorderedMatches;
            }
        }

        for (int toIdx = 0; toIdx < toSize; ++toIdx) {
            int paramMatch = getListMatch(toParamTypes, toIdx, fromParamTypes);
            if (paramMatch == toIdx) {
                ++exactMatches;
            }
            else if (paramMatch >= 0) {
                ++misorderedMatches;
            }
        }

        int numParams = Math.max(fromSize, toSize);
        double match = (double)exactMatches / numParams;
        match += (double)misorderedMatches / (2 * numParams);

        return 0.5 + (match / 2.0);
    }

    /**
     * Returns 0 for exact match, +1 for misordered match, -1 for no match.
     */
    public int getListMatch(List<String> fromList, int fromIndex, List<String> toList) {
        int fromSize = fromList.size();
        int toSize = toList.size();
        String fromStr = fromIndex < fromSize ? fromList.get(fromIndex) : null;
        String toStr = fromIndex < toSize ? toList.get(fromIndex) : null;
        
        if (fromStr == null) {
            return -1;
        }
        
        if (fromStr.equals(toStr)) {
            fromList.set(fromIndex, null);
            toList.set(fromIndex, null);
            return fromIndex;
        }
        
        for (int toIdx = 0; toIdx < toSize; ++toIdx) {
            toStr = toList.get(toIdx);
            if (fromStr.equals(toStr)) {
                fromList.set(fromIndex, null);
                toList.set(toIdx, null);
                return toIdx;
            }
        }
        return -1;
    }

    public int getExactMatch(List<ASTFormalParameter> fromParameters, ASTFormalParameter to) {
        int idx = 0;
        for (ASTFormalParameter from : fromParameters) {
            if (areTypesEqual(from, to) && areNamesEqual(from, to)) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    public boolean areTypesEqual(ASTFormalParameter from, ASTFormalParameter to) {
        if (from == null) {
            return false;
        }
        Parameter fromParam = new Parameter(from);
        Parameter toParam = new Parameter(to);
        return fromParam.isTypeEqual(toParam);
    }

    public boolean areNamesEqual(ASTFormalParameter from, ASTFormalParameter to) {
        if (from == null) {
            return false;
        }
        Parameter fromParam = new Parameter(from);
        Parameter toParam = new Parameter(to);
        return fromParam.isNameEqual(toParam);
    }

    public Integer[] getMatch(int fromIdx, Parameters toParams) {
        List<ASTFormalParameter> fromFormalParams = getParameterList();
        List<ASTFormalParameter> toFormalParams = toParams.getParameterList();

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

}
