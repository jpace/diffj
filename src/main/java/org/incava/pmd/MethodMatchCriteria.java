package org.incava.pmd;

import java.util.*;
import net.sourceforge.pmd.ast.*;
import org.incava.java.MatchCriteria;


/**
 * A criterion (some criteria) for matching nodes.
 */
public class MethodMatchCriteria extends MatchCriteria {
    
    private final ASTMethodDeclaration meth;

    private String name = null;

    private ASTFormalParameters params = null;
    
    public MethodMatchCriteria(ASTMethodDeclaration m) {
        meth = m;
    }

    public double compare(MatchCriteria other) {
        if (other instanceof MethodMatchCriteria) {
            MethodMatchCriteria mmother = (MethodMatchCriteria)other;
            
            String aName = getName();
            String bName = mmother.getName();

            double score = 0.0;

            if (aName.equals(bName)) {
                ASTFormalParameters afp = getParameters();
                ASTFormalParameters bfp = mmother.getParameters();

                score = ParameterUtil.getMatchScore(afp, bfp);
            }
            else {
                // this could eventually find methods renamed, if we compare based
                // on parameters and method contents
            }

            return score;
        }
        else {
            return super.compare(other);
        }
    }

    protected String getName() {
        // lazy evaluation
        if (name == null) {
            name = MethodUtil.getName(meth).image;
        }
        return name;
    }

    protected ASTFormalParameters getParameters() {
        // lazy evaluation
        if (params == null) {
            params = MethodUtil.getParameters(meth);
        }
        return params;
    }

}
