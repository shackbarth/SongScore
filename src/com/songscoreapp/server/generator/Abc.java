package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;

public class Abc {

    private static String PREAMBLE1 = ""
        + "X:1\n"
        + "T:";
    private static String PREAMBLE2 = "\n"
        + "M:4/4\n"
        + "L:1/8\n"
        + "K:G\n";

    /**
     * melody rhythm, and lyrics are parallel lists indexed by note
     * chords are indexed by line
     */
    public static String getAbc(ArrayList<ArrayList<String>> lyrics, List<Integer> chords,
            List<List<Integer>> melody, List<List<Integer>> rhythm, String seed) {

        StringBuffer abc = new StringBuffer();
        abc.append(PREAMBLE1);
        abc.append(seed);
        abc.append(PREAMBLE2);

        System.out.println("rhythm: "+ rhythm);
        System.out.println("melody: "+ melody);

        for (int i = 0; i < rhythm.size(); ++i) {
            int wordIndex = 0;
            List<Integer> lineRhythm = rhythm.get(i);
            List<Integer> lineMelody = melody.get(i);
            List<String>  lineLyrics = lyrics.get(i);

            System.out.println("lineLyrics: "+ lineLyrics);

            String chord1 = "\"" + Util.chordToLetter(chords.get(2 * i)) + "\"";
            String chord2 = "\"" + Util.chordToLetter(chords.get(2 * i + 1)) + "\"";

            StringBuffer musicBuf = new StringBuffer();
            StringBuffer lyricBuf = new StringBuffer();
            int syllableIndex = 0;
            int eighthNotes = 0;
            int measure = 0;
            for (int j = 0; j < lineRhythm.size(); ++j) {
            	Integer noteDuration = 0;
                Integer restDuration = 0;
            	if (j == 0 && lineRhythm.get(j) > 0) {
            		noteDuration = 0;
                    musicBuf.append("z" + (lineRhythm.get(0) == 1 ? "" : lineRhythm.get(0).toString()));
                    eighthNotes += lineRhythm.get(0);
            	}
                String note = Util.integerToLetter(lineMelody.get(j));

                if (j == lineRhythm.size() - 1) {
                    noteDuration = 16 - lineRhythm.get(j);
                	//System.out.println("noteDuration: "+ noteDuration);
                }
                else {
                    noteDuration = lineRhythm.get(j + 1) - lineRhythm.get(j);
                }
                if (noteDuration > 2) {
                    restDuration = noteDuration - 2;
                    noteDuration = 2;
                }
                if (noteDuration > 0) {
	                if (noteDuration + eighthNotes == 8) {
	                    musicBuf.append(note + noteDuration.toString());
	                    musicBuf.append("|");
	                    if (++measure == 1) {
	                    	musicBuf.append(chord2);
	                    }
	                    eighthNotes = 0;
	                }
	                else if (noteDuration + eighthNotes > 8) {
	                    musicBuf.append(note + new Integer(8 - eighthNotes).toString());
	                    noteDuration -= (8 - eighthNotes);
	                    musicBuf.append("|");
	                    if (++measure == 1) {
	                    	musicBuf.append(chord2);
	                    }
	                    eighthNotes = 0;
	                    // there  has to be at least 1 left over...
	                    musicBuf.append(note + (noteDuration == 1 ? "" : noteDuration.toString()));
	                    eighthNotes += noteDuration;
	                }
	                else {
	                    musicBuf.append(note + (noteDuration == 1 ? "" : noteDuration.toString()));
	                    eighthNotes += noteDuration;
	                }
                }

                if (restDuration > 0) {
                    if (restDuration + eighthNotes == 8) {
                        musicBuf.append("z" + restDuration.toString());
                        musicBuf.append("|");
                        if (++measure == 1) {
                        	musicBuf.append(chord2);
                        }
                        eighthNotes = 0;
                    }
                    else if (restDuration + eighthNotes > 8) {
                        musicBuf.append("z" + new Integer(8 - eighthNotes).toString());
                        restDuration -= (8 - eighthNotes);
                        musicBuf.append("|");
                        if (++measure == 1) {
                        	musicBuf.append(chord2);
                        }
                        eighthNotes = 0;
                        // there  has to be at least 1 left over...
                        musicBuf.append("z" + (restDuration == 1 ? "" : restDuration.toString()));
                        eighthNotes += restDuration;
                    }
                    else {
                        musicBuf.append("z" + (restDuration == 1 ? "" : restDuration.toString()));
                        eighthNotes += restDuration;
                    }
                }

                List<String> syllableList = SyllableUtil.getSyllablesFromWord(lineLyrics.get(wordIndex));
                String syllable = syllableList.get(syllableIndex);
                lyricBuf.append(syllable);

                if (syllableIndex < syllableList.size() - 1) {
                    lyricBuf.append("-");
                    syllableIndex++;
                }
                else {
                    lyricBuf.append(" ");
                    wordIndex++;
                    syllableIndex = 0;
                }
            }
            abc.append(chord1);
            abc.append(musicBuf);
            abc.append("\n");
            abc.append("w: "+ lyricBuf);
            abc.append("\n");
        }
        //System.out.println(abc.toString());
        return abc.toString();
    }
}
