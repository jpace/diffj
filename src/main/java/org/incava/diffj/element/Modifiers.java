package org.incava.diffj.element;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Token;
import org.incava.diffj.Messages;
import org.incava.diffj.element.Differences;
import org.incava.pmdx.SimpleNodeUtil;

public abstract class Modifiers {
    private final SimpleNode node;

    public Modifiers(SimpleNode node) {
        this.node = node;
    }

    public abstract int[] getModifierTypes();

    public List<Token> getLeadingTokens() { 
        return SimpleNodeUtil.getLeadingTokens(node);
    }

    public Token getFirstToken() {
        return node.getFirstToken();
    }

    public void diff(Modifiers toModifiers, Differences differences) {
        int[] modifierTypes = getModifierTypes();

        Map<Integer, Token> fromByKind = getModifierMap();
        Map<Integer, Token> toByKind = toModifiers.getModifierMap();

        for (int modType : modifierTypes) {
            Token fromMod = fromByKind.get(modType);
            Token toMod = toByKind.get(modType);
            
            if (fromMod == null) {
                if (toMod != null) {
                    differences.changed(getFirstToken(), toMod, Messages.MODIFIER_ADDED, toMod.image);
                }
            }
            else if (toMod == null) {
                differences.changed(fromMod, toModifiers.getFirstToken(), Messages.MODIFIER_REMOVED, fromMod.image);
            }
        }
    }

    /**
     * Returns a map from token types ("kinds", as integers), to the token. This
     * assumes that there are no leading tokens of the same type for the given
     * node.
     */
    protected Map<Integer, Token> getModifierMap() {
        List<Token> mods = getLeadingTokens();
        Map<Integer, Token> byKind = new TreeMap<Integer, Token>();

        for (Token tk : mods) {
            byKind.put(tk.kind, tk);
        }

        return byKind;
    }
}
