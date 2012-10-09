package org.incava.diffj;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.analysis.FileDiffs;
import org.incava.pmdx.*;

public abstract class Modifiers {
    private final SimpleNode node;

    public Modifiers(SimpleNode node) {
        this.node = node;
    }

    public abstract int[] getModifierTypes();

    public void diff(SimpleNode toNode, Differences differences) {
        int[] modifierTypes = getModifierTypes();
        List<Token> fromMods = SimpleNodeUtil.getLeadingTokens(node);
        List<Token> toMods = SimpleNodeUtil.getLeadingTokens(toNode);

        Map<Integer, Token> fromByKind = getModifierMap(node);
        Map<Integer, Token> toByKind = getModifierMap(toNode);
        
        for (int mi = 0; mi < modifierTypes.length; ++mi) {
            Integer modInt  = Integer.valueOf(modifierTypes[mi]);
            Token   fromMod = fromByKind.get(modInt);
            Token   toMod   = toByKind.get(modInt);

            if (fromMod == null) {
                if (toMod != null) {
                    differences.changed(node.getFirstToken(), toMod, Messages.MODIFIER_ADDED, toMod.image);
                }
            }
            else if (toMod == null) {
                differences.changed(fromMod, toNode.getFirstToken(), Messages.MODIFIER_REMOVED, fromMod.image);
            }
        }
    }

    /**
     * Returns a map from token types ("kinds", as integers), to the token. This
     * assumes that there are no leading tokens of the same type for the given
     * node.
     */
    protected Map<Integer, Token> getModifierMap(SimpleNode node) {
        List<Token> mods = SimpleNodeUtil.getLeadingTokens(node);
        Map<Integer, Token> byKind = new TreeMap<Integer, Token>();

        for (Token tk : mods) {
            byKind.put(tk.kind, tk);
        }

        tr.Ace.onBlue("byKind", byKind);

        return byKind;
    }
}
