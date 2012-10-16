package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import org.incava.diffj.element.Differences;

public class ParameterExactMatch extends ParameterMatch {
    public ParameterExactMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
    }

    public void diff(Differences differences) {
        // no difference.
    }
}