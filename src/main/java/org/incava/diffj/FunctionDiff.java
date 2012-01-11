package org.incava.diffj;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiff;
import org.incava.analysis.Report;
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

    public FunctionDiff(Report report) {
        super(report);
    }

    public FunctionDiff(Collection<FileDiff> differences) {
        super(differences);
    }

    protected void compareReturnTypes(SimpleNode a, SimpleNode b) {
        SimpleNode art    = (SimpleNode)a.jjtGetChild(0);
        SimpleNode brt    = (SimpleNode)b.jjtGetChild(0);
        String     artStr = SimpleNodeUtil.toString(art);
        String     brtStr = SimpleNodeUtil.toString(brt);
        // tr.Ace.log("art: " + art + "; brt: " + brt);

        if (artStr.equals(brtStr)) {
            // tr.Ace.log("no change in return types");
        }
        else {
            changed(art, brt, RETURN_TYPE_CHANGED, artStr, brtStr);
        }
    }

    protected void markParametersAdded(ASTFormalParameters afp, ASTFormalParameters bfp) {
        Token[] names = ParameterUtil.getParameterNames(bfp);
        for (int ni = 0; ni < names.length; ++ni) {
            changed(afp, names[ni], PARAMETER_ADDED, names[ni].image);
        }
    }

    protected void markParametersRemoved(ASTFormalParameters afp, ASTFormalParameters bfp) {
        Token[] names = ParameterUtil.getParameterNames(afp);
        for (int ni = 0; ni < names.length; ++ni) {                
            changed(names[ni], bfp, PARAMETER_REMOVED, names[ni].image);
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
        tr.Ace.log("aParams", aParams);
        List<Parameter> bParams = ParameterUtil.getParameterList(bfp);
        tr.Ace.log("bParams", bParams);
        
        List<String> aParamTypes = ParameterUtil.getParameterTypes(afp);
        tr.Ace.log("aParamTypes", aParamTypes);
        List<String> bParamTypes = ParameterUtil.getParameterTypes(bfp);
        tr.Ace.log("bParamTypes", bParamTypes);

        int aSize = aParamTypes.size();
        int bSize = bParamTypes.size();

        // tr.Ace.log("aParamTypes.size: " + aSize + "; bParamTypes.size: " + bSize);

        if (aSize == 0) {
            if (bSize != 0) {
                markParametersAdded(afp, bfp);
            }
        }
        else if (bSize == 0) {
            markParametersRemoved(afp, bfp);
        }
        else {
            for (int ai = 0; ai < aSize; ++ai) {
                tr.Ace.log("ai", String.valueOf(ai));
                
                Parameter ap = aParams.get(ai);
                tr.Ace.log("ap", ap);

                int[] paramMatch = ParameterUtil.getMatch(aParams, ai, bParams);

                tr.Ace.log("paramMatch", paramMatch);

                ASTFormalParameter aParam = ParameterUtil.getParameter(afp, ai);

                if (paramMatch[0] == ai && paramMatch[1] == ai) {
                    // tr.Ace.log("exact match");
                }
                else if (paramMatch[0] == ai) {
                    markParameterNameChanged(aParam, bfp, ai);
                }
                else if (paramMatch[1] == ai) {
                    markParameterTypeChanged(ap, bfp, ai);
                }
                else if (paramMatch[0] >= 0) {
                    checkForReorder(aParam, ai, bfp, paramMatch[0]);
                }
                else if (paramMatch[1] >= 0) {
                    markReordered(aParam, ai, bfp, paramMatch[1]);
                }
                else {
                    markRemoved(aParam, bfp);
                }
            }

            Iterator<Parameter> bit = bParams.iterator();
            for (int bi = 0; bit.hasNext(); ++bi) {
                Parameter bp = bit.next();
                if (bp != null) {
                    ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, bi);
                    Token bName = ParameterUtil.getParameterName(bParam);
                    changed(afp, bParam, PARAMETER_ADDED, bName.image);
                }
            }
        }
    }

    protected void compareThrows(SimpleNode a, ASTNameList at, SimpleNode b, ASTNameList bt) {
        if (at == null) {
            if (bt != null) {
                ASTName[] names = (ASTName[])SimpleNodeUtil.findChildren(bt, ASTName.class);
                for (ASTName name : names) {
                    changed(a, name, THROWS_ADDED, SimpleNodeUtil.toString(name));
                }
            }
        }
        else if (bt == null) {
            ASTName[] names = (ASTName[])SimpleNodeUtil.findChildren(at, ASTName.class);
            for (ASTName name : names) {
                changed(name, b, THROWS_REMOVED, SimpleNodeUtil.toString(name));
            }
        }
        else {
            ASTName[] aNames = (ASTName[])SimpleNodeUtil.findChildren(at, ASTName.class);
            ASTName[] bNames = (ASTName[])SimpleNodeUtil.findChildren(bt, ASTName.class);

            for (int ai = 0; ai < aNames.length; ++ai) {
                // save a reference to the name here, in case it gets removed
                // from the array in getMatch.
                ASTName aName = aNames[ai];

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

            for (int bi = 0; bi < bNames.length; ++bi) {
                if (bNames[bi] != null) {
                    ASTName bName = ThrowsUtil.getNameNode(bt, bi);
                    changed(at, bName, THROWS_ADDED, SimpleNodeUtil.toString(bName));
                }
            }
        }
    }

    protected int getMatch(ASTName[] aNames, int aIndex, ASTName[] bNames) {
        String aNameStr = SimpleNodeUtil.toString(aNames[aIndex]);

        for (int bi = 0; bi < bNames.length; ++bi) {
            if (bNames[bi] != null && SimpleNodeUtil.toString(bNames[bi]).equals(aNameStr)) {
                aNames[aIndex] = null;
                bNames[bi]     = null; // mark as consumed
                return bi;
            }
        }

        return -1;
    }
}
