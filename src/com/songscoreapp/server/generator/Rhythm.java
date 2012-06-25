package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rhythm {

    /**
     * Each line is two bars of 4/4. Rhythm can happen on eighth notes. The notes are 0-indexed.
     * So 0 is the downbeat of the first bar. 3 is the upbeat of beat 2 of the first bar.
     * {0, 3} means that there's a note on the downbeat and the upbeat of 2.
     * {1, 8} means that there's a note on the upbeat of 1 and the downbeat of the second bar
     * these numbers can therefore range from 0 to 15.
     */
    public static List<List<Integer>> getRhythm(ArrayList<ArrayList<String>> lyrics) {
        List<List<Integer>> rhythm = new ArrayList<List<Integer>>();
        boolean halfDouble = true;
        for(List<String> line : lyrics) {
            int syllableCount = SyllableUtil.getSyllableCountFromLine(line);
            List<Integer> rhythmLine = getRhythmLine(syllableCount, halfDouble);
            halfDouble = !halfDouble;
            rhythm.add(rhythmLine);
        }
        return rhythm;
    }

    /**
     * Doublehalf:
     * returns a rhythm line where the two bars of the line have identical rhythm
     */
    private static List<Integer> getRhythmLine(Integer syllableCount, boolean doubleHalf) {
        boolean isOdd = false;
        Integer optionCount = 16;

        if(doubleHalf) {
            isOdd = syllableCount % 2 == 1;
            syllableCount = (syllableCount + 1) / 2;
            optionCount /= 2;
        }

        List<Integer> rhythmLine = new ArrayList<Integer>();

        /**
         * pre-populate all the available rhythm options
         */
        List<Integer> rhythmOptions = new ArrayList<Integer>();
        for(int i = 0; i < optionCount; i++) {
            rhythmOptions.add(i);
        }

        /**
         * choose notes randomly. We go through each available note, from the first,
         * and decide whether to choose it. The way we've set it up, notes are more
         * likely to get selected if they're early in the bar, or on the offbeats,
         * because that's what rock & roll is all about.
         */
        Double earlyPreference = 1.5;
        Double syncopatedPreference = 1.5;
        for(int iNote = 0; iNote < 8; iNote++) {
            boolean noteHasBeenChosen = false;
            if(iNote < syllableCount) {
                for(int iOptions = 0; iOptions < rhythmOptions.size(); iOptions++) {
                    double chance = 1.0 * earlyPreference / rhythmOptions.size();
                    if(rhythmOptions.get(iOptions) % 2 == 1) {
                        chance *= syncopatedPreference;
                    }
                    if(Math.random() < chance) {
                        rhythmLine.add(rhythmOptions.get(iOptions));
                        if(doubleHalf) {
                            rhythmLine.add(rhythmOptions.get(iOptions) + 8);
                        }
                        rhythmOptions.remove(iOptions);
                        noteHasBeenChosen = true;
                        break;
                    }
                }
                /**
                 * Still no note chosen? Just throw in the first
                 */
                if(!noteHasBeenChosen) {
                    rhythmLine.add(rhythmOptions.get(0));
                    if(doubleHalf) {
                        rhythmLine.add(rhythmOptions.get(0) + 8);
                    }
                    rhythmOptions.remove(0);
                }
            }
        }
        Collections.sort(rhythmLine);
        if(isOdd) {
            // remove the last note
            rhythmLine.remove(rhythmLine.size() - 1);
        }
        return rhythmLine;
    }

}
