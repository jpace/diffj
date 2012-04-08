package org.incava.diffj;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.Parameter;
import org.incava.pmdx.ParameterUtil;
import org.incava.pmdx.SimpleNodeUtil;
import org.incava.pmdx.ThrowsUtil;

public class FunctionDiff extends ItemDiff {
    public static final String RETURN_TYPE_CHANGED = "return type changed from {0} to {1}";

    public static final String PARAMETER_REMOVED = "parameter removed: {0}";
    public static final String PARAMETER_ADDED = "parameter added: {0}";

    public static final String PARAMETER_REORDERED = "parameter {0} reordered from argument {1} to {2}";

    public static final String PARAMETER_TYPE_CHANGED = "parameter type changed from {0} to {1}";    

    public static final String PARAMETER_NAME_CHANGED = "parameter name changed from {0} to {1}";

    public static final String PARAMETER_REORDERED_AND_RENAMED = "parameter {0} reordered from argument {1} to {2} and renamed {3}";

    public static final String THROWS_REMOVED = "throws removed: {0}";
    public static final String THROWS_ADDED = "throws added: {0}";

    public static final String THROWS_REORDERED = "throws {0} reordered from argument {1} to {2}";

    public FunctionDiff(FileDiffs differences) {
        super(differences);
    }

    public List<ASTName> getChildNames(ASTNameList nameList) {
        return SimpleNodeUtil.snatchChildren(nameList, "net.sourceforge.pmd.ast.ASTName");
    }

    protected void compareReturnTypes(SimpleNode fromNode, SimpleNode toNode) {
        SimpleNode fromRetType    = SimpleNodeUtil.findChild(fromNode);
        SimpleNode toRetType      = SimpleNodeUtil.findChild(toNode);
        String     fromRetTypeStr = SimpleNodeUtil.toString(fromRetType);
        String     toRetTypeStr   = SimpleNodeUtil.toString(toRetType);

        if (!fromRetTypeStr.equals(toRetTypeStr)) {
            changed(fromRetType, toRetType, RETURN_TYPE_CHANGED, fromRetTypeStr, toRetTypeStr);
        }
    }

    protected void markParametersAdded(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams) {
        List<Token> names = ParameterUtil.getParameterNames(toFormalParams);
        for (Token name : names) {
            changed(fromFormalParams, name, PARAMETER_ADDED, name.image);
        }
    }

    protected void markParametersRemoved(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams) {
        List<Token> names = ParameterUtil.getParameterNames(fromFormalParams);
        for (Token name : names) {
            changed(name, toFormalParams, PARAMETER_REMOVED, name.image);
        }
    }

    protected void markParameterTypeChanged(ASTFormalParameter fromParam, ASTFormalParameters toFormalParams, int idx) {
        ASTFormalParameter toParam = ParameterUtil.getParameter(toFormalParams, idx);
        String toType = ParameterUtil.getParameterType(toParam);
        changed(fromParam, toParam, PARAMETER_TYPE_CHANGED, ParameterUtil.getParameterType(fromParam), toType);
    }

    protected void markParameterNameChanged(ASTFormalParameter fromParam, ASTFormalParameters toFormalParams, int idx) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        Token toNameTk = ParameterUtil.getParameterName(toFormalParams, idx);
        changed(fromNameTk, toNameTk, PARAMETER_NAME_CHANGED, fromNameTk.image, toNameTk.image);
    }

    protected void checkForReorder(ASTFormalParameter fromParam, int fromIdx, ASTFormalParameters toFormalParams, int toIdx) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        Token toNameTk = ParameterUtil.getParameterName(toFormalParams, toIdx);
        if (fromNameTk.image.equals(toNameTk.image)) {
            changed(fromNameTk, toNameTk, PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
        }
        else {
            changed(fromNameTk, toNameTk, PARAMETER_REORDERED_AND_RENAMED, fromNameTk.image, fromIdx, toIdx, toNameTk.image);
        }
    }

    protected void markReordered(ASTFormalParameter fromParam, int fromIdx, ASTFormalParameters toParams, int toIdx) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        ASTFormalParameter toParam = ParameterUtil.getParameter(toParams, toIdx);
        changed(fromParam, toParam, PARAMETER_REORDERED, fromNameTk.image, fromIdx, toIdx);
    }

    protected void markRemoved(ASTFormalParameter fromParam, ASTFormalParameters toParams) {
        Token fromNameTk = ParameterUtil.getParameterName(fromParam);
        changed(fromParam, toParams, PARAMETER_REMOVED, fromNameTk.image);
    }

    protected void compareParameters(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams) {
        tr.Ace.setVerbose(true);

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

        tr.Ace.setVerbose(false);
    }

    /**
     * Compares each parameter. Assumes that the lists are the same size.
     */
    protected void compareEachParameter(ASTFormalParameters fromFormalParams, ASTFormalParameters toFormalParams, int size) {
        List<Parameter> fromParams = ParameterUtil.getParameterList(fromFormalParams);
        List<Parameter> toParams = ParameterUtil.getParameterList(toFormalParams);

        for (int idx = 0; idx < size; ++idx) {
            Parameter fromParam = fromParams.get(idx);
            ASTFormalParameter fromPrm = fromParam.getParameter();

            int[] paramMatch = ParameterUtil.getMatch(fromParams, idx, toParams);

            ASTFormalParameter fromFormalParam = ParameterUtil.getParameter(fromFormalParams, idx);

            if (paramMatch[0] == idx && paramMatch[1] == idx) {
                continue;
            }
            else if (paramMatch[0] == idx) {
                markParameterNameChanged(fromFormalParam, toFormalParams, idx);
            }
            else if (paramMatch[1] == idx) {
                markParameterTypeChanged(fromPrm, toFormalParams, idx);
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

        Iterator<Parameter> toIt = toParams.iterator();
        for (int toIdx = 0; toIt.hasNext(); ++toIdx) {
            Parameter toParam = toIt.next();
            if (toParam != null) {
                ASTFormalParameter toFormalParam = ParameterUtil.getParameter(toFormalParams, toIdx);
                Token toName = ParameterUtil.getParameterName(toFormalParam);
                changed(fromFormalParams, toFormalParam, PARAMETER_ADDED, toName.image);
            }
        }
    }

    protected void changeThrows(SimpleNode fromNode, SimpleNode toNode, String msg, ASTName name) {
        changed(fromNode, toNode, msg, SimpleNodeUtil.toString(name));
    }

    protected void addAllThrows(SimpleNode fromNode, ASTNameList toNameList) {
        List<ASTName> names = getChildNames(toNameList);
        for (ASTName name : names) {
            changeThrows(fromNode, name, THROWS_ADDED, name);
        }
    }

    protected void removeAllThrows(ASTNameList fromNameList, SimpleNode toNode) {
        List<ASTName> names = getChildNames(fromNameList);
        for (ASTName name : names) {
            changeThrows(name, toNode, THROWS_REMOVED, name);
        }
    }

    protected void compareEachThrow(ASTNameList fromNameList, ASTNameList toNameList) {
        List<ASTName> fromNames = getChildNames(fromNameList);
        List<ASTName> toNames = getChildNames(toNameList);

        for (int fromIdx = 0; fromIdx < fromNames.size(); ++fromIdx) {
            // save a reference to the name here, in case it gets removed
            // from the array in getMatch.
            ASTName fromName = fromNames.get(fromIdx);

            int throwsMatch = getMatch(fromNames, fromIdx, toNames);

            if (throwsMatch == fromIdx) {
                continue;
            }
            else if (throwsMatch >= 0) {
                ASTName toName = ThrowsUtil.getNameNode(toNameList, throwsMatch);
                String fromNameStr = SimpleNodeUtil.toString(fromName);
                changed(fromName, toName, THROWS_REORDERED, fromNameStr, fromIdx, throwsMatch);
            }
            else {
                changeThrows(fromName, toNameList, THROWS_REMOVED, fromName);
            }
        }

        for (int toIdx = 0; toIdx < toNames.size(); ++toIdx) {
            if (toNames.get(toIdx) != null) {
                ASTName toName = ThrowsUtil.getNameNode(toNameList, toIdx);
                changeThrows(fromNameList, toName, THROWS_ADDED, toName);
            }
        }
    }
    
    protected void compareThrows(SimpleNode fromNode, ASTNameList fromNameList, SimpleNode toNode, ASTNameList toNameList) {
        if (fromNameList == null) {
            if (toNameList != null) {
                addAllThrows(fromNode, toNameList);
            }
        }
        else if (toNameList == null) {
            removeAllThrows(fromNameList, toNode);
        }
        else {
            compareEachThrow(fromNameList, toNameList);
        }
    }

    protected int getMatch(List<ASTName> fromNames, int fromIdx, List<ASTName> toNames) {
        String fromNameStr = SimpleNodeUtil.toString(fromNames.get(fromIdx));

        for (int toIdx = 0; toIdx < toNames.size(); ++toIdx) {
            if (toNames.get(toIdx) != null && SimpleNodeUtil.toString(toNames.get(toIdx)).equals(fromNameStr)) {
                fromNames.set(fromIdx, null);
                toNames.set(toIdx, null); // mark as consumed
                return toIdx;
            }
        }

        return -1;
    }
}
