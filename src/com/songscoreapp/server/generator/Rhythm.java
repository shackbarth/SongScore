package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rhythm {

    /**
     * 0 = never choose off-beat notes
     * 1 = choose off-beat notes with the same frequency as on-beat notes
     * 2 = be twice as likely to choose off-beat notes
     * 999999 = always choose off-beat notes
     */
    final static double SYNCOPATION_PREFERENCE = 2;




    /**
     * Each line is two bars of 4/4. Rhythm can happen on eighth notes. The notes are 0-indexed.
     * So 0 is the downbeat of the first bar. 3 is the upbeat of beat 2 of the first bar.
     * {0, 3} means that there's a note on the downbeat and the upbeat of 2.
     * {1, 8} means that there's a note on the upbeat of 1 and the downbeat of the second bar
     * these numbers can therefore range from 0 to 15.
     */
    public static List<List<Integer>> getRhythm(List<String> lyrics) {
        List<List<Integer>> rhythm = new ArrayList<List<Integer>>();
        boolean halfDouble = true;
        for(String line : lyrics) {
            List<Integer> wordLengths = SyllableUtil.getWordSyllablesFromLine(line);
            //System.out.println(wordLengths);

            // come up with the rhythm of each word independently to keep them tight
            int totalSpotsAvailable = 16;
            List<List<Integer>> wordClusters = new ArrayList<List<Integer>>();
            for (int wordLength : wordLengths) {
                ArrayList<Integer> wordCluster = new ArrayList<Integer>();
                wordCluster.add(0);
                int place = 0;
                for(int i = 1 ; i < wordLength; i++) {
                    // each note within a word is either an eight note or a quarter note
                    int length = Math.random() < 0.5 ? 1 : 2;
                    place += length;
                    wordCluster.add(place);
                }
                wordClusters.add(wordCluster);
                totalSpotsAvailable -= (place + 1);
            }
            //System.out.println(wordClusters);
            //System.out.println(totalSpotsAvailable);

            // determine the spaces
            // between the words (and before and after them all)
            ArrayList<Integer> betweenWordSpaces = new ArrayList<Integer>();
            for(int i = 0; i <= wordLengths.size(); i++) {
                betweenWordSpaces.add(0);
            }
            while(totalSpotsAvailable >= 0) {
                double END_SPACE_PREFERENCE = 1.5;
                int spaceToAddTo = (int) (Math.random() * betweenWordSpaces.size() * END_SPACE_PREFERENCE);
                spaceToAddTo = Math.min(spaceToAddTo, betweenWordSpaces.size() - 1);
                betweenWordSpaces.set(spaceToAddTo, 1 + betweenWordSpaces.get(spaceToAddTo));
                totalSpotsAvailable--;
            }
            //System.out.println(betweenWordSpaces);

            // now arrange the word clusters in the line by putting it all together
            List<Integer> rhythmLine = new ArrayList<Integer>();
            int place = 0;
            while(true) {
                // move forward the place by the length of the space
                int space = betweenWordSpaces.remove(0);
                place += space;
                if(betweenWordSpaces.size() == 0) {
                    break;
                }

                List<Integer> wordCluster = wordClusters.remove(0);
                for(int wordSyllable : wordCluster) {
                    rhythmLine.add(place + wordSyllable);
                }
                // move forward the place by the length of the word
                place += (wordCluster.get(wordCluster.size() - 1) + 1);
            }
            //System.out.println(rhythmLine);
            rhythm.add(rhythmLine);

            // old implementation:
            //int syllableCount = SyllableUtil.getSyllableCountFromLine(line);
            //List<Integer> rhythmLine = getRhythmLine(syllableCount, halfDouble);
            //halfDouble = !halfDouble;
            //rhythm.add(rhythmLine);
        }
        return rhythm;
    }

    /**
     * Doublehalf:
     * returns a rhythm line where the two bars of the line have identical rhythm
     */
    private static List<Integer> getRhythmLine(Integer syllableCount, boolean doubleHalf) {
        boolean isOdd = false;

        /**
         * Singleton notes that straggle at the end of the line don't rock. So we constrain ourselves
         * to choose notes within the cordon, which is some range big enough that we still have plenty
         * of notes to choose from but smaller than the entire span.
         */
        Integer cordon = (16 + syllableCount) / 2;

        if(doubleHalf) {
            isOdd = syllableCount % 2 == 1;
            syllableCount = (syllableCount + 1) / 2;
            cordon = (cordon + 1) / 2;
        }

        List<Integer> rhythmLine = new ArrayList<Integer>();

        /**
         * pre-populate all the available rhythm options
         */
        ArrayList<Integer> rhythmOptions = new ArrayList<Integer>();
        for(int i = 0; i < cordon; i++) {
            rhythmOptions.add(i);
        }
        for(int iNote = 0; iNote < syllableCount; iNote++) {
            Integer chosenNote = chooseNote(rhythmOptions);
            rhythmLine.add(chosenNote);
            if(doubleHalf) {
                rhythmLine.add(chosenNote + 8);
            }
            rhythmOptions.remove(chosenNote);
        }
        Collections.sort(rhythmLine);
        if(isOdd) {
            // remove the last note
            rhythmLine.remove(rhythmLine.size() - 1);
        }
        return rhythmLine;
    }

    /**
     * Chooses one of the integers of the options. We prefer
     * @param options
     * @return
     */
    static Integer chooseNote(List<Integer> notes) {

        List<Integer> oddNotes = new ArrayList<Integer>();
        List<Integer> evenNotes = new ArrayList<Integer>();
        for(Integer note : notes) {
            if(note % 2 == 0) {
                evenNotes.add(note);
            } else {
                oddNotes.add(note);
            }
        }
        double oddChance = oddNotes.size() * SYNCOPATION_PREFERENCE
                / (evenNotes.size() + (oddNotes.size() * SYNCOPATION_PREFERENCE));
        boolean chooseOdd = Math.random() < oddChance;
        if(chooseOdd) {
            return oddNotes.get((int) (Math.random() * oddNotes.size()));
        } else {
            return evenNotes.get((int) (Math.random() * evenNotes.size()));
        }


    }

}
