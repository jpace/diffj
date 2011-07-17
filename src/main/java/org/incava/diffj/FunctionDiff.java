package org.incava.diffj;

import java.awt.Point;
import java.io.*;
import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.analysis.*;
import org.incava.ijdk.util.*;
import org.incava.ijdk.util.diff.*;
import org.incava.java.*;
import org.incava.pmd.*;


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
            if (bSize == 0) {
                // tr.Ace.log("no change in parameters");
            }
            else {
                Token[] names = ParameterUtil.getParameterNames(bfp);
                for (int ni = 0; ni < names.length; ++ni) {
                    changed(afp, names[ni], PARAMETER_ADDED, names[ni].image);
                }
            }
        }
        else if (bSize == 0) {
            Token[] names = ParameterUtil.getParameterNames(afp);
            for (int ni = 0; ni < names.length; ++ni) {                
                changed(names[ni], bfp, PARAMETER_REMOVED, names[ni].image);
            }
        }
        else {
            for (int ai = 0; ai < aSize; ++ai) {
                tr.Ace.log("ai", String.valueOf(ai));
                
                Parameter ap = aParams.get(ai);
                tr.Ace.log("ap", ap);

                int[] paramMatch = ParameterUtil.getMatch(aParams, ai, bParams);

                tr.Ace.log("paramMatch", paramMatch);

                ASTFormalParameter aParam = ParameterUtil.getParameter(afp, ai);

                Token aNameTk = ParameterUtil.getParameterName(aParam);

                if (paramMatch[0] == ai && paramMatch[1] == ai) {
                    // tr.Ace.log("exact match");
                }
                else if (paramMatch[0] == ai) {
                    // tr.Ace.log("name changed");
                    Token bNameTk = ParameterUtil.getParameterName(bfp, ai);
                    changed(aNameTk, bNameTk, PARAMETER_NAME_CHANGED, aNameTk.image, bNameTk.image);
                }
                else if (paramMatch[1] == ai) {
                    // tr.Ace.log("type changed");
                    ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, ai);
                    String             bType  = ParameterUtil.getParameterType(bParam);
                    // tr.Ace.log("bParam: " + bParam + "; bType: " + bType);

                    changed(ap.getParameter(), bParam, PARAMETER_TYPE_CHANGED, ap.getType(), bType);
                }
                else if (paramMatch[0] >= 0) {
                    // tr.Ace.log("misordered match by type");
                    Token bNameTk = ParameterUtil.getParameterName(bfp, paramMatch[0]);
                    // tr.Ace.log("aNameTk: " + aNameTk + "; bNameTk: " + bNameTk);
                    // tr.Ace.log("aNameTk.image: " + aNameTk.image + "; bNameTk.image: " + bNameTk.image);
                    if (aNameTk.image.equals(bNameTk.image)) {
                        changed(aNameTk, bNameTk, PARAMETER_REORDERED, aNameTk.image, ai, paramMatch[0]);
                    }
                    else {
                        changed(aNameTk, bNameTk, PARAMETER_REORDERED_AND_RENAMED, aNameTk.image, ai, paramMatch[0], bNameTk.image);
                    }
                }
                else if (paramMatch[1] >= 0) {
                    System.out.println("misordered match by name");
                    
                    tr.Ace.log("misordered match by name");

                    ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, paramMatch[1]);

                    changed(aParam, bParam, PARAMETER_REORDERED, aNameTk.image, ai, paramMatch[1]);
                }
                else {
                    // tr.Ace.log("not a match");
                    // tr.Ace.log("aNameTk: " + aNameTk);

                    changed(aParam, bfp, PARAMETER_REMOVED, aNameTk.image);
                }
            }

            // tr.Ace.log("aParams: " + aParams);
            // tr.Ace.log("bParams: " + bParams);

            Iterator<Parameter> bit = bParams.iterator();
            for (int bi = 0; bit.hasNext(); ++bi) {
                Parameter bp = bit.next();
                if (bp == null) {
                    // tr.Ace.log("already processed");
                }
                else {
                    ASTFormalParameter bParam = ParameterUtil.getParameter(bfp, bi);
                    Token bName = ParameterUtil.getParameterName(bParam);
                    // tr.Ace.log("bName: " + bName);
                    changed(afp, bParam, PARAMETER_ADDED, bName.image);
                }
            }
        }
    }

    protected void compareThrows(SimpleNode a, ASTNameList at, SimpleNode b, ASTNameList bt) {
        if (at == null) {
            if (bt == null) {
                // tr.Ace.log("no change in throws");
            }
            else {
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

                // tr.Ace.log("throwsMatch: " + throwsMatch);

                if (throwsMatch == ai) {
                    // tr.Ace.log("exact match");
                }
                else if (throwsMatch >= 0) {
                    // tr.Ace.log("misordered match");
                    ASTName bName = ThrowsUtil.getNameNode(bt, throwsMatch);
                    // tr.Ace.log("aName: " + aName + "; bName: " + bName);
                    String aNameStr = SimpleNodeUtil.toString(aName);
                    changed(aName, bName, THROWS_REORDERED, aNameStr, ai, throwsMatch);
                }
                else {
                    // tr.Ace.log("not a match; aName: " + aName);
                    changed(aName, bt, THROWS_REMOVED, SimpleNodeUtil.toString(aName));
                }
            }

            for (int bi = 0; bi < bNames.length; ++bi) {
                // tr.Ace.log("b: " + bNames[bi]);

                if (bNames[bi] == null) {
                    // tr.Ace.log("already processed");
                }
                else {
                    ASTName bName = ThrowsUtil.getNameNode(bt, bi);
                    // tr.Ace.log("bName: " + bName);
                    changed(at, bName, THROWS_ADDED, SimpleNodeUtil.toString(bName));
                }
            }
        }
    }

    protected int getMatch(ASTName[] aNames, int aIndex, ASTName[] bNames) {
        String aNameStr = SimpleNodeUtil.toString(aNames[aIndex]);

        // tr.Ace.log("aNameStr: " + aNameStr);

        for (int bi = 0; bi < bNames.length; ++bi) {
            if (bNames[bi] == null) {
                // tr.Ace.log("already consumed");
            }
            else if (SimpleNodeUtil.toString(bNames[bi]).equals(aNameStr)) {
                aNames[aIndex] = null;
                bNames[bi]     = null;
                return bi;
            }
        }

        return -1;
    }

}
