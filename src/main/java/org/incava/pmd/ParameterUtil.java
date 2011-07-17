package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;


/**
 * Miscellaneous routines for parameters.
 */
public class ParameterUtil extends SimpleNodeUtil {

    public static ASTFormalParameter[] getParameters(ASTFormalParameters params) {
        return (ASTFormalParameter[])findChildren(params, ASTFormalParameter.class);
    }

    public static Token[] getParameterNames(ASTFormalParameters params) {
        ASTFormalParameter[] fps = getParameters(params);
        List<Token> names = new ArrayList<Token>();
        
        for (int pi = 0; pi < fps.length; ++pi) {
            ASTFormalParameter fp = fps[pi];
            Token name = getParameterName(fp);
            names.add(name);
        }

        return names.toArray(new Token[0]);
    }

    public static ASTFormalParameter getParameter(ASTFormalParameters params, int index) {
        return (ASTFormalParameter)findChild(params, ASTFormalParameter.class, index);
    }

    public static Token getParameterName(ASTFormalParameters params, int index) {
        ASTFormalParameter param = getParameter(params, index);
        return getParameterName(param);
    }

    public static String getParameterType(ASTFormalParameters params, int index) {
        ASTFormalParameter param = getParameter(params, index);
        return getParameterType(param);
    }

    public static List<String> getParameterTypes(ASTFormalParameters params) {
        List<String> types = new ArrayList<String>();
        int  nParams = params.jjtGetNumChildren();
        for (int i = 0; i < nParams; ++i) {
            ASTFormalParameter param = (ASTFormalParameter)params.jjtGetChild(i);
            String             type  = new Parameter(param).getType();
            types.add(type);
        }
        return types;
    }

    public static List<Parameter> getParameterList(ASTFormalParameters params) {
        List<Parameter> paramList = new ArrayList<Parameter>();
        int nParams = params.jjtGetNumChildren();
        
        for (int i = 0; i < nParams; ++i) {
            ASTFormalParameter param  = getParameter(params, i);
            paramList.add(new Parameter(param));
        }

        return paramList;
    }

    public static Token getParameterName(ASTFormalParameter param) {
        if (param == null) {
            return null;
        }
        else {
            ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId)param.jjtGetChild(1);
            return vid.getFirstToken();
        }
    }

    public static String getParameterType(ASTFormalParameter param) {
        if (param == null) {
            return null;
        }
        else {
            // type is the first child, but we also have to look for the
            // variable ID including brackets, for arrays
            StringBuffer typeBuf = new StringBuffer();
            ASTType      type    = (ASTType)SimpleNodeUtil.findChild(param, ASTType.class);
            Token        ttk     = type.getFirstToken();
        
            while (true) {
                typeBuf.append(ttk.image);
                if (ttk == type.getLastToken()) {
                    break;
                }
                else {
                    ttk = ttk.next;
                }
            }
            
            ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId)SimpleNodeUtil.findChild(param, ASTVariableDeclaratorId.class);
            
            Token vtk = vid.getFirstToken();
            while (vtk != vid.getLastToken()) {
                vtk = vtk.next;
                typeBuf.append(vtk.image);
            }

            return typeBuf.toString();
        }
    }

    public static int[] getMatch(List<Parameter> aParameters, int aIndex, List<Parameter> bParameters) {
        int typeMatch = -1;
        int nameMatch = -1;
        
        Parameter ap = aParameters.get(aIndex);

        for (int bi = 0; bi < bParameters.size(); ++bi) {
            Parameter bp = bParameters.get(bi);

            if (bp != null) {
                if (ap.getType().equals(bp.getType())) {
                    typeMatch = bi;
                }

                if (ap.getName().equals(bp.getName())) {
                    nameMatch = bi;
                }

                if (typeMatch == bi && nameMatch == bi) {
                    aParameters.set(aIndex, null);
                    bParameters.set(bi,     null);
                    return new int[] { typeMatch, nameMatch };
                }
            }
        }

        int bestMatch = typeMatch;
        if (bestMatch < 0) {
            bestMatch = nameMatch;
        }

        if (bestMatch >= 0) {
            // make sure there isn't an exact match for this somewhere else in
            // aParameters
            Parameter bp = bParameters.get(bestMatch);

            int aMatch = getExactMatch(aParameters, bp);

            if (aMatch >= 0) {
                return new int[] { -1, -1 };
            }
            else {
                aParameters.set(aIndex,    null);
                bParameters.set(bestMatch, null);
                return new int[] { typeMatch, nameMatch };
            }
        }
        else {
            return new int[] { -1, -1 };
        }
    }

    public static double getMatchScore(ASTFormalParameters a, ASTFormalParameters b) {
        double score;
        
        if (a.jjtGetNumChildren() == 0 && b.jjtGetNumChildren() == 0) {
            score = 1.0;
        }
        else {
            // (int[], double, String) <=> (int[], double, String) ==> 100% (3 of 3)
            // (int[], double, String) <=> (double, int[], String) ==> 80% (3 of 3 - 10% * misordered)
            // (int[], double)         <=> (double, int[], String) ==> 46% (2 of 3 - 10% * misordered)
            // (int[], double, String) <=> (String) ==> 33% (1 of 3 params)
            // (int[], double) <=> (String) ==> 0 (0 of 3)

            List<String> aParamTypes = getParameterTypes(a);
            List<String> bParamTypes = getParameterTypes(b);

            int aSize = aParamTypes.size();
            int bSize = bParamTypes.size();

            int exactMatches = 0;
            int misorderedMatches = 0;
            
            for (int ai = 0; ai < aSize; ++ai) {
                int paramMatch = getListMatch(aParamTypes, ai, bParamTypes);
                if (paramMatch == ai) {
                    ++exactMatches;
                }
                else if (paramMatch >= 0) {
                    ++misorderedMatches;
                }
            }

            for (int bi = 0; bi < bSize; ++bi) {
                int paramMatch = getListMatch(bParamTypes, bi, aParamTypes);
                if (paramMatch == bi) {
                    ++exactMatches;
                }
                else if (paramMatch >= 0) {
                    ++misorderedMatches;
                }
            }

            int numParams = Math.max(aSize, bSize);
            double match = (double)exactMatches / numParams;
            match += (double)misorderedMatches / (2 * numParams);

            score = 0.5 + (match / 2.0);
        }
        
        return score;
    }

    /**
     * Returns 0 for exact match, +1 for misordered match, -1 for no match.
     */
    protected static int getListMatch(List<String> aList, int aIndex, List<String> bList) {
        int    aSize = aList.size();
        int    bSize = bList.size();
        String aStr  = aIndex < aSize ? aList.get(aIndex) : null;
        String bStr  = aIndex < bSize ? bList.get(aIndex) : null;
        
        if (aStr == null) {
            return -1;
        }
        if (aStr.equals(bStr)) {
            aList.set(aIndex, null);
            bList.set(aIndex, null);
            return aIndex;
        }
        else {
            for (int bi = 0; bi < bSize; ++bi) {
                bStr = bList.get(bi);
                if (aStr.equals(bStr)) {
                    aList.set(aIndex, null);
                    bList.set(bi, null);
                    return bi;
                }
            }
            return -1;
        }
    }

    protected static int getExactMatch(List<Parameter> parameters, Parameter other) {
        int idx = 0;
        for (Parameter param : parameters) {
            if (param != null && param.getType().equals(other.getType()) && param.getName().equals(other.getName())) {
                return idx;
            }
            else {
                ++idx;
            }
        }
        return -1;
    }

}
