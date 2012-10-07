package org.incava.diffj;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.*;

public class ModifiersDiff extends DiffComparator {    
    public static final String MODIFIER_REMOVED = "modifier removed: {0}";
    public static final String MODIFIER_ADDED = "modifier added: {0}";
    public static final String MODIFIER_CHANGED = "modifier changed from {0} to {1}";

    public ModifiersDiff(FileDiffs differences) {
        super(differences);
    }

    /**
     * Returns a map from token types ("kinds", as java.lang.Integers), to the
     * token. This assumes that there are no leading tokens of the same type for
     * the given node.
     */
    protected Map<Integer, Token> getModifierMap(SimpleNode node) {
        List<Token>         mods   = SimpleNodeUtil.getLeadingTokens(node);        
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
                    changed(aNode.getFirstToken(), bMod, MODIFIER_ADDED, bMod.image);
                }
            }
            else if (bMod == null) {
                changed(aMod, bNode.getFirstToken(), MODIFIER_REMOVED, aMod.image);
            }
        }
    }
}
