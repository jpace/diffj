package org.incava.diffj.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.incava.ijdk.util.IUtil;
import org.incava.attest.Parameterized;
import org.junit.Test;

import static org.incava.attest.Assertions.assertEqual;
import static org.incava.attest.Assertions.message;

public class ListComparatorTest extends Parameterized {
    public Map<Integer, Integer> map(Integer ... vals) {
        Map<Integer, Integer> m = new HashMap<Integer, Integer>();
        Integer k = null;
        for (Integer val : vals) {
            if (k == null) {
                k = val;
            }
            else {
                m.put(k, val);
                k = null;
            }
        }
        return m;
    }

    @Test
    @Parameters
    @TestCaseName("{method} {index} {params}")
    public <T> void test(List<Integer> expExactMatches, Map<Integer, Integer> expMisorderedMatches, List<T> from, List<T> to) {   
        ListComparator<T> lc = new ListComparator<T>(from, to);
        ListComparison comp = lc.getComparison();
        assertEqual(expExactMatches,      comp.getExactMatches(),      message("from", from, "to", to));
        assertEqual(expMisorderedMatches, comp.getMisorderedMatches(), message("from", from, "to", to));
    }
    
    private List<Object[]> parametersForTest() {
        return paramsList(params(IUtil.<Integer>list(), map(),                 IUtil.<String>list(),      IUtil.<String>list()),
                          params(IUtil.list(0),         map(),                 IUtil.list("x"),           IUtil.list("x")),
                          params(IUtil.list(0, 1),      map(),                 IUtil.list("x", "y"),      IUtil.list("x", "y")),
                          params(IUtil.list(0, 1),      map(),                 IUtil.list("x", "y", "z"), IUtil.list("x", "y", "a")),
                          params(IUtil.<Integer>list(), map(0, 1, 1, 0),       IUtil.list("x", "y"),      IUtil.list("y", "x")),
                          params(IUtil.<Integer>list(), map(0, 1, 1, 2, 2, 0), IUtil.list("x", "y", "z"), IUtil.list("z", "x", "y")),
                          params(IUtil.<Integer>list(), map(0, 1, 1, 2, 2, 0), IUtil.list("x", "y", "z"), IUtil.list("z", "x", "y", "a")));
    }
}
