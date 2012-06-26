package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;

public class Melody {

    /**
     * Returns a list of lines.
     * Each line is a list of integers that represent the pitches of the various notes on that line.
     * 1 is do, 2 is re, 3 is mi. 8 is do in the higher octave, etc.
     *
     * Each voice part will have its own range limitations
     */
    public static List<List<Integer>> getMelody(String key, Character voicePart, List<List<String>> lyrics,
            List<List<Integer>> rhythm, List<Integer> chords) {
        // TODO: allow other voice parts
        if(!key.equals("G") || !voicePart.equals('B')) {
            // bass parts in G major are the only ones we are supporting!
            return null;
        }

        int lowestNote = 2; // low A
        int highestNote = 12; // high D

        List<List<Integer>> melody = new ArrayList<List<Integer>>();
        /**
         * Create the melody on line at a time
         */
        for(int iLine = 0; iLine < lyrics.size(); iLine++) {
            List<Integer> melodyLine = new ArrayList<Integer>();
            Integer chosenNote = -1;
            List<Integer> rhythmLine = rhythm.get(iLine);
            for(Integer rhythmEvent : rhythmLine) {
                Integer chordContext = rhythmEvent < 8 ? chords.get(iLine * 2) : chords.get(iLine * 2 + 1);
                Double random = Math.random();
                if(chosenNote < 0) {
                    // start in the top octave
                    chosenNote = getRandomNoteInContext(highestNote - 7, highestNote, chosenNote, chordContext);
                } else if(random < 0.4) {
                    // jump to a note within a fourth in the correct context
                    chosenNote = getRandomNoteInContext(
                        Math.max(lowestNote, chosenNote - 3),
                        Math.min(highestNote, chosenNote + 3),
                        chosenNote,
                        chordContext);
                } else if(random < 0.6 && chosenNote < highestNote) {
                    chosenNote++;
                } else if(random < 0.8 && chosenNote > lowestNote) {
                    chosenNote--;
                }

                melodyLine.add(chosenNote);
            }



            melody.add(melodyLine);
        }
        return melody;
    }

    protected static Integer getRandomNoteInContext(int min, int max, int currentNote, Integer chordContext) {
        List<Integer> possibleNotes = new ArrayList<Integer>();
        for(int i = min; i <= max; i++) {
            if(i == currentNote) {
                continue;
            }
            int noteInChord = (i + 7 - chordContext) % 7;
            if(noteInChord == 0 || noteInChord == 2 || noteInChord == 4) {
                possibleNotes.add(i);
            }
        }
        int choice = (int) (Math.random() * possibleNotes.size());
        return possibleNotes.get(choice);
    }
}
