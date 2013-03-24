package com.songscoreapp.server.generator;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class RhythmTest {

    @Test
    public void testRhythm() {
        List<String> lyrics = Arrays.asList(new String[] {
                "origami peanut butter",
                "seven crazy men"});

        List<List<Integer>> rhythm = Rhythm.getRhythm(lyrics);
        System.out.println(rhythm);
    }
}
