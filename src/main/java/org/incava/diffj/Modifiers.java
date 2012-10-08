package org.incava.diffj;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.*;

public class Modifiers {    
    private final DiffComparator differences;

    public Modifiers(FileDiffs fileDiffs) {
        this.differences = new DiffComparator(fileDiffs);
    }

    /**
     * Returns a map from token types ("kinds", as java.lang.Integers), to the
     * token. This assumes that there are no leading tokens of the same type for
     * the given node.
     */
    protected Map<Integer, Token> getModifierMap(SimpleNode node) {
        List<Token> mods = SimpleNodeUtil.getLeadingTokens(node);        
        Map<Integer, Token> byKind = new TreeMap<Integer, Token>();

        for (Token tk : mods) {
            byKind.put(Integer.valueOf(tk.kind), tk);
        }

        return byKind;
    }

    public void compareModifiers(SimpleNode aNode, SimpleNode bNode, int[] modifierTypes) {
        List<Token> aMods = SimpleNodeUtil.getLeadingTokens(aNode);
        List<Token> bMods = SimpleNodeUtil.getLeadingTokens(bNode);

        Map<Integer, Token> aByKind = getModifierMap(aNode);
        Map<Integer, Token> bByKind = getModifierMap(bNode);
        
        for (int mi = 0; mi < modifierTypes.length; ++mi) {
            Integer modInt = Integer.valueOf(modifierTypes[mi]);
            Token   aMod   = aByKind.get(modInt);
            Token   bMod   = bByKind.get(modInt);

            if (aMod == null) {
                if (bMod != null) {
                    differences.changed(aNode.getFirstToken(), bMod, Messages.MODIFIER_ADDED, bMod.image);
                }
            }
            else if (bMod == null) {
                differences.changed(aMod, bNode.getFirstToken(), Messages.MODIFIER_REMOVED, aMod.image);
            }
        }
    }
}
