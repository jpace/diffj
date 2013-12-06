package org.incava.diffj.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.incava.test.IncavaTestCase;
import org.incava.ijdk.util.IUtil;
import static org.incava.ijdk.util.IUtil.*;

public class TestParameterTypes extends IncavaTestCase {
    public TestParameterTypes(String name) {
        super(name);
    }

    public void assertMatchScore(double expected, List<String> fromParamTypes, List<String> toParamTypes) {
        ParameterTypes fpt = new ParameterTypes(fromParamTypes);
        ParameterTypes tpt = new ParameterTypes(toParamTypes);
        assertEquals("fromTypes: " + fromParamTypes + "; toTypes: " + toParamTypes, expected, fpt.getMatchScore(tpt));
    }

    public void testGetMatchScoreExactMatchEmpty() {
        assertMatchScore(3, IUtil.<String>list(), IUtil.<String>list());
    }

    public void testGetMatchScoreExactMatchOneArgument() {
        assertMatchScore(3, list("int"), list("int"));
    }

    public void testGetMatchScoreExactMatchTwoArguments() {
        assertMatchScore(3, list("int", "String"), list("int", "String"));
    }

    public void testGetMatchScoreTwoBothCommon() {
        assertMatchScore(2, list("int", "String"), list("String", "int"));
    }

    public void testGetMatchScoreThreeTwoCommonOneRepeated() {
        assertMatchScore(2, list("int", "String", "String"), list("String", "String", "int"));
    }

    public void testGetMatchScoreThreeAllCommonAllDifferentOrder() {
        assertMatchScore(2, list("int", "String", "byte[]"), list("String", "byte[]", "int"));
    }

    public void testGetMatchScoreFromIncludesToOneParam() {
        assertMatchScore(1, list("int"), IUtil.<String>list());
    }

    public void testGetMatchScoreFromIncludesToTwoParams() {
        assertMatchScore(1, list("int", "String"), list("String"));
    }

    public void testGetMatchScoreTwoParamsNoCommon() {
        assertMatchScore(1, list("int", "String"), list("double"));
    }

    public void testGetMatchScoreThreeIncludesTwoMisordered() {
        assertMatchScore(1, list("int", "String"), list("String", "int", "double"));
    }

    public void testGetMatchScoreFiveIncludesThreeMisordered() {
        assertMatchScore(1, list("int", "double", "String"), list("String", "int", "byte[]", "double", "float"));
    }

    public void testGetMatchScoreFourIncludesThreeMisordered() {
        assertMatchScore(1, list("int", "double", "String"), list("String", "int", "byte[]", "double"));
    }

    public void testGetMatchScoreThreeIncludesOneMisordered() {
        assertMatchScore(1, list("int", "String", "double"), list("double"));
    }
}
