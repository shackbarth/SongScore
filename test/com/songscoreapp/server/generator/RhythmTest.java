package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class RhythmTest {

    @Test
    public void testRhythm() {
        final int syllableCount = 8;
        List<String> line = new ArrayList<String>();
        for(int i = 0; i < syllableCount; i++) {
            line.add("la");
        }

        List<List<String>> lyrics = new ArrayList<List<String>>();
        lyrics.add(line);
        lyrics.add(line);

        List<List<Integer>> rhythm = Rhythm.getRhythm(lyrics);
        System.out.println(rhythm);
    }
}
