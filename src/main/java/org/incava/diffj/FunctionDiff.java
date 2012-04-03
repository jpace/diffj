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

    protected void compareReturnTypes(SimpleNode a, SimpleNode b) {
        SimpleNode art    = (SimpleNode)a.jjtGetChild(0);
        SimpleNode brt    = (SimpleNode)b.jjtGetChild(0);
        String     artStr = SimpleNodeUtil.toString(art);
        String     brtStr = SimpleNodeUtil.toString(brt);

        if (!artStr.equals(brtStr)) {
            changed(art, brt, RETURN_TYPE_CHANGED, artStr, brtStr);
        }
    }

    protected void markParametersAdded(ASTFormalParameters afp, ASTFormalParameters bfp) {
        List<Token> names = ParameterUtil.getParameterNames(bfp);
        for (Token name : names) {
            changed(afp, name, PARAMETER_ADDED, name.image);
        }
    }

    protected void markParametersRemoved(ASTFormalParameters afp, ASTFormalParameters bfp) {
        List<Token> names = ParameterUtil.getParameterNames(afp);
        for (Token name : names) {
            changed(name, bfp, PARAMETER_REMOVED, name.image);
        }
    }

    protected void markParameterTypeChanged(Parameter ap, ASTFormalParameters bfp, int idx) {
        ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, idx);
        String             bType  = ParameterUtil.getParameterType(bParam);
        changed(ap.getParameter(), bParam, PARAMETER_TYPE_CHANGED, ap.getType(), bType);
    }

    protected void markParameterNameChanged(ASTFormalParameter aParam, ASTFormalParameters bfp, int idx) {
        Token aNameTk = ParameterUtil.getParameterName(aParam);
        Token bNameTk = ParameterUtil.getParameterName(bfp, idx);
        changed(aNameTk, bNameTk, PARAMETER_NAME_CHANGED, aNameTk.image, bNameTk.image);
    }

    protected void checkForReorder(ASTFormalParameter aParam, int aidx, ASTFormalParameters bfp, int bidx) {
        Token aNameTk = ParameterUtil.getParameterName(aParam);
        Token bNameTk = ParameterUtil.getParameterName(bfp, bidx);
        if (aNameTk.image.equals(bNameTk.image)) {
            changed(aNameTk, bNameTk, PARAMETER_REORDERED, aNameTk.image, aidx, bidx);
        }
        else {
            changed(aNameTk, bNameTk, PARAMETER_REORDERED_AND_RENAMED, aNameTk.image, aidx, bidx, bNameTk.image);
        }
    }

    protected void markReordered(ASTFormalParameter aParam, int aidx, ASTFormalParameters bParams, int bidx) {
        Token aNameTk = ParameterUtil.getParameterName(aParam);
        ASTFormalParameter bParam = ParameterUtil.getParameter(bParams, bidx);
        changed(aParam, bParam, PARAMETER_REORDERED, aNameTk.image, aidx, bidx);
    }

    protected void markRemoved(ASTFormalParameter aParam, ASTFormalParameters bParams) {
        Token aNameTk = ParameterUtil.getParameterName(aParam);
        changed(aParam, bParams, PARAMETER_REMOVED, aNameTk.image);
    }

    protected void compareParameters(ASTFormalParameters afp, ASTFormalParameters bfp) {
        List<Parameter> aParams = ParameterUtil.getParameterList(afp);
        List<Parameter> bParams = ParameterUtil.getParameterList(bfp);
        
        List<String> aParamTypes = ParameterUtil.getParameterTypes(afp);
        List<String> bParamTypes = ParameterUtil.getParameterTypes(bfp);

        int aSize = aParamTypes.size();
        int bSize = bParamTypes.size();

        if (aSize > 0) {
            if (bSize > 0) {
                compareEachParameter(afp, aParams, bfp, bParams, aSize);
            }
            else {
                markParametersRemoved(afp, bfp);
            }
        }
        else if (bSize > 0) {
            markParametersAdded(afp, bfp);
        }
    }

    /**
     * Compares each parameter. Assumes that the lists are the same size.
     */
    protected void compareEachParameter(ASTFormalParameters afp, List<Parameter> aParams, ASTFormalParameters bfp, List<Parameter> bParams, int size) {
        for (int idx = 0; idx < size; ++idx) {
            Parameter ap = aParams.get(idx);

            int[] paramMatch = ParameterUtil.getMatch(aParams, idx, bParams);

            ASTFormalParameter aParam = ParameterUtil.getParameter(afp, idx);

            if (paramMatch[0] == idx && paramMatch[1] == idx) {
                // tr.Ace.log("exact match");
            }
            else if (paramMatch[0] == idx) {
                markParameterNameChanged(aParam, bfp, idx);
            }
            else if (paramMatch[1] == idx) {
                markParameterTypeChanged(ap, bfp, idx);
            }
            else if (paramMatch[0] >= 0) {
                checkForReorder(aParam, idx, bfp, paramMatch[0]);
            }
            else if (paramMatch[1] >= 0) {
                markReordered(aParam, idx, bfp, paramMatch[1]);
            }
            else {
                markRemoved(aParam, bfp);
            }
        }

        Iterator<Parameter> bit = bParams.iterator();
        for (int bidx = 0; bit.hasNext(); ++bidx) {
            Parameter bp = bit.next();
            tr.Ace.onYellow("bp", bp);
            if (bp != null) {
                ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, bidx);
                Token bName = ParameterUtil.getParameterName(bParam);
                changed(afp, bParam, PARAMETER_ADDED, bName.image);
            }
        }
    }

    protected void compareThrows(SimpleNode a, ASTNameList at, SimpleNode b, ASTNameList bt) {
        if (at == null) {
            if (bt != null) {
                List<ASTName> names = SimpleNodeUtil.snatchChildren(bt, "net.sourceforge.pmd.ast.ASTName");
                for (ASTName name : names) {
                    changed(a, name, THROWS_ADDED, SimpleNodeUtil.toString(name));
                }
            }
        }
        else if (bt == null) {
            List<ASTName> names = SimpleNodeUtil.snatchChildren(at, "net.sourceforge.pmd.ast.ASTName");
            for (ASTName name : names) {
                changed(name, b, THROWS_REMOVED, SimpleNodeUtil.toString(name));
            }
        }
        else {
            List<ASTName> aNames = SimpleNodeUtil.snatchChildren(at, "net.sourceforge.pmd.ast.ASTName");
            List<ASTName> bNames = SimpleNodeUtil.snatchChildren(bt, "net.sourceforge.pmd.ast.ASTName");

            for (int ai = 0; ai < aNames.size(); ++ai) {
                // save a reference to the name here, in case it gets removed
                // from the array in getMatch.
                ASTName aName = aNames.get(ai);

                int throwsMatch = getMatch(aNames, ai, bNames);

                if (throwsMatch == ai) {
                    // tr.Ace.log("exact match");
                }
                else if (throwsMatch >= 0) {
                    ASTName bName = ThrowsUtil.getNameNode(bt, throwsMatch);
                    String aNameStr = SimpleNodeUtil.toString(aName);
                    changed(aName, bName, THROWS_REORDERED, aNameStr, ai, throwsMatch);
                }
                else {
                    changed(aName, bt, THROWS_REMOVED, SimpleNodeUtil.toString(aName));
                }
            }

            for (int bi = 0; bi < bNames.size(); ++bi) {
                if (bNames.get(bi) != null) {
                    ASTName bName = ThrowsUtil.getNameNode(bt, bi);
                    changed(at, bName, THROWS_ADDED, SimpleNodeUtil.toString(bName));
                }
            }
        }
    }

    protected int getMatch(List<ASTName> aNames, int aIndex, List<ASTName> bNames) {
        String aNameStr = SimpleNodeUtil.toString(aNames.get(aIndex));

        for (int bi = 0; bi < bNames.size(); ++bi) {
            if (bNames.get(bi) != null && SimpleNodeUtil.toString(bNames.get(bi)).equals(aNameStr)) {
                aNames.set(aIndex, null);
                bNames.set(bi,     null); // mark as consumed
                return bi;
            }
        }

        return -1;
    }
}
