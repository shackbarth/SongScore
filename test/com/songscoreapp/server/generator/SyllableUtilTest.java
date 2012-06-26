package com.songscoreapp.server.generator;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.Test;

public class SyllableUtilTest {

    @Test
    public void testIsMultisyllableVowelCluster() {
        assertEquals(false, SyllableUtil.isMultisyllableVowelCluster("e"));
        assertEquals(true, SyllableUtil.isMultisyllableVowelCluster("iou"));
        assertEquals(true, SyllableUtil.isMultisyllableVowelCluster("io"));
        assertEquals(false, SyllableUtil.isMultisyllableVowelCluster("ea"));
        assertEquals(true, SyllableUtil.isMultisyllableVowelCluster("ia"));
    }

    @Test
    public void testGetClustersFromWord() {
        ArrayList testList = new ArrayList();
        testList.add("g");
        assertEquals(testList, SyllableUtil.getClustersFromWord("g"));

        testList = new ArrayList();
        testList.add("b");
        testList.add("oo");
        testList.add("kk");
        testList.add("ee");
        testList.add("p");
        testList.add("e");
        testList.add("r");
        assertEquals(testList, SyllableUtil.getClustersFromWord("bookkeeper"));

        testList = new ArrayList();
        testList.add(null);
        testList.add("a");
        assertEquals(testList, SyllableUtil.getClustersFromWord("a"));
    }

    @Test
    public void testGetSyllablesFromWord() {
        ArrayList testList = new ArrayList();
        testList.add("a");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("a"));

        testList = new ArrayList();
        testList.add("ya");
        // TODO: deal with this one
        //assertEquals(testList, SyllableUtil.getSyllablesFromWord("ya"));

        testList = new ArrayList();
        testList.add("the");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("the"));

        testList = new ArrayList();
        testList.add("be");
        testList.add("ne");
        testList.add("lux");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("benelux"));

        testList = new ArrayList();
        testList.add("book");
        testList.add("kee");
        testList.add("per");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("bookkeeper"));

        testList = new ArrayList();
        testList.add("swam");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("swam"));

        testList = new ArrayList();
        testList.add("li");
        testList.add("ons");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("lions"));

        testList = new ArrayList();
        testList.add("type");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("type"));

        testList = new ArrayList();
        testList.add("ly");
        testList.add("rics");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("lyrics"));

        testList = new ArrayList();
        testList.add("here");
        assertEquals(testList, SyllableUtil.getSyllablesFromWord("here"));
    }
}
