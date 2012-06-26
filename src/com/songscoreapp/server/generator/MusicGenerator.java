package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;

public class MusicGenerator {

    static private final String SONG_STRUCTURE = "VVRVRVRR";

    public static String getSheetMusicFromInput(String goodLine, Character voicePart) {
        String key = "G";
        voicePart = 'B'; // obviously it's a cheat that we secretly change this to bass

        List<Integer> chords = new ArrayList<Integer>();
        List<List<String>> lyrics = new ArrayList<List<String>>();
        List<List<Integer>> rhythm = new ArrayList<List<Integer>>();
        List<List<Integer>> melody = new ArrayList<List<Integer>>();

        List<Integer> refrainChords = Chords.getChords();
        List<List<String>> refrainLyrics = Lyrics.getLyrics(goodLine);
        List<List<Integer>> refrainRhythm = Rhythm.getRhythm(refrainLyrics);
        List<List<Integer>> refrainMelody = Melody.getMelody(key, voicePart, refrainLyrics, refrainRhythm, refrainChords);

        List<Integer> verseChords = Chords.getChords();

        for(int i = 0; i < SONG_STRUCTURE.length(); i++) {
            if(SONG_STRUCTURE.charAt(i) == 'R') {
                chords.addAll(refrainChords);
                lyrics.addAll(refrainLyrics);
                rhythm.addAll(refrainRhythm);
                melody.addAll(refrainMelody);
            } else if(SONG_STRUCTURE.charAt(i) == 'V') {
                chords.addAll(verseChords);
                List<List<String>> verseLyrics = Lyrics.getLyrics(goodLine);
                lyrics.addAll(verseLyrics);
                // TODO: really the rhythm and the melody should be pretty much the same
                // for all verses, with minor tweaks for varying syllable count
                List<List<Integer>> verseRhythm = Rhythm.getRhythm(verseLyrics);
                rhythm.addAll(verseRhythm);
                List<List<Integer>> verseMelody = Melody.getMelody(key, voicePart, verseLyrics, verseRhythm, verseChords);
                melody.addAll(verseMelody);

            } else {
                // one day a bridge?
            }
        }

        String abc = Abc.getAbc(lyrics, chords, melody, rhythm, goodLine);
        return abc;
    }
}
