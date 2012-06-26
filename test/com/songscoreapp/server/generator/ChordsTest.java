package com.songscoreapp.server.generator;

import java.util.List;
import org.junit.Test;

public class ChordsTest {

    @Test
    public void testGetChords() {
        for(int i = 0; i < 10; i++) {
            List<Integer> chords = Chords.getChords();
            System.out.println(chords);
        }
    }
}
