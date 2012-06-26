package com.songscoreapp.server.generator;

import java.util.List;

public class MusicGenerator {

    public static String getSheetMusicFromInput(String goodLine, Character voicePart) {
        String key = "G";
        voicePart = 'B'; // obviously it's a cheat that we secretly change this to bass

        List<List<String>> lyrics = Lyrics.getLyrics(goodLine);
        List<Integer> chords = Chords.getChords();
        List<List<Integer>> rhythm = Rhythm.getRhythm(lyrics);
        List<List<Integer>> melody = Melody.getMelody(key, voicePart, lyrics, rhythm, chords);
        //System.out.println(melody);
        String abc = Abc.getAbc(lyrics, chords, melody, rhythm, goodLine);
        return abc;
    }
}
