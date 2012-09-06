package com.songscoreapp.server.generator;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class AbcTest {

    @Test
    public void testMe() {
        List<String> lyrics = Arrays.asList(new String[] {"la la la la la la la"});

        List<Integer> chords = new ArrayList<Integer>();
        chords.add(1);
        chords.add(5);

        List<Integer> melodyLine = new ArrayList<Integer>();
        for(int i = 0; i < 7; i++) {
            melodyLine.add(4 + i);
        }
        List<List<Integer>> melody = new ArrayList<List<Integer>>();
        melody.add(melodyLine);

        List<Integer> rhythmLine = new ArrayList<Integer>();
        rhythmLine.add(0);
        rhythmLine.add(1);
        rhythmLine.add(3);
        rhythmLine.add(4);
        rhythmLine.add(5);
        rhythmLine.add(7);
        rhythmLine.add(10);
        List<List<Integer>> rhythm = new ArrayList<List<Integer>>();
        rhythm.add(rhythmLine);

        String abc = Abc.getAbc(lyrics, chords, melody, rhythm, "seed");
        System.out.println(abc);
        assertTrue(abc.contains("\"G\"CD2EFG2A1|\"D\"z2B2z4|"));
    }
}
