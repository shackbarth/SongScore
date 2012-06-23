package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;

public class Chords {

    public static int[][] possibilities = {
        {},
        {3, 4, 5},
        {4, 5},
        {4, 6},
        {1, 5},
        {1, 6},
        {2, 4, 5}
    };

    /**
     * 1 is the chord on do
     * 2 is the chord on re
     * etc.
     *
     * there is no chord 7
     *
     * note that we presuppose a verse of 4 lines * 2 bars/line * 1 chord/bar = 8 chords
     */

    public static List<Integer> getChords() {
        List<Integer> chords = new ArrayList<Integer>();
        chords.add(1);
        for(int i = 1; i < 8; i++) {
            int previousChord = chords.get(i - 1);
            int[] nextChordPossibility = possibilities[previousChord];
            double choice = Math.random() * nextChordPossibility.length;
            int chordChoice = possibilities[previousChord][(int) choice];
            chords.add(chordChoice);
            // TODO: the eighth chord should always be 1, 4, or 5
        }
        return chords;
    }

}
