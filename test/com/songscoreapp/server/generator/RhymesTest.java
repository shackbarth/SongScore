package com.songscoreapp.server.generator;

import org.junit.Test;

public class RhymesTest {
    @Test
    public void testGetRhymes() {
        System.out.println(new Rhymes().getRhymes("ay"));
        System.out.println(new Rhymes().getRhymes("robe"));
    }
}
