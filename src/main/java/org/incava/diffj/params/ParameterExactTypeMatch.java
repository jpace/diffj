package org.incava.diffj.params;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Messages;
import org.incava.diffj.element.Differences;

/**
 * The type matches for the given index.
 */
public class ParameterExactTypeMatch extends ParameterMatch {
    public ParameterExactTypeMatch(ASTFormalParameter fromFormalParam, int index, int typeMatch, int nameMatch, Parameters toParams) {
        super(fromFormalParam, index, typeMatch, nameMatch, toParams);
    }

    public void diff(Differences differences) {
        Token fromNameTk = getParameterName();
        Token toNameTk = toParams.getParameterName(index);
        differences.changed(fromNameTk, toNameTk, Messages.PARAMETER_NAME_CHANGED, fromNameTk.image, toNameTk.image);
    }
}
