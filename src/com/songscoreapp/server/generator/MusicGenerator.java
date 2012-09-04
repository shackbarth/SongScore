package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.songscoreapp.server.objectify.DbLoader;
import com.songscoreapp.server.objectify.RhymingDictionary;
import com.songscoreapp.server.objectify.Word;

public class MusicGenerator {

    static private final String SONG_STRUCTURE = "VVRVRVRR";
    Objectify ofy;
    RhymingDictionary dictionary;
    Lyrics lyricWriter;

    public MusicGenerator() {
        super();
        ofy = ObjectifyService.begin();
        try {
            ObjectifyService.register(Word.class);
        } catch(IllegalArgumentException e) {
        }
        dictionary = new RhymingDictionary(ofy);
        lyricWriter = new Lyrics(dictionary);
    }



    public String getSheetMusicFromInput(String goodLine, Character voicePart) {
        if(goodLine != null && goodLine.equals("Admin: load words")) {
            DbLoader loader = new DbLoader(ofy);
            loader.loadWords("src/com/songscoreapp/server/resources/words-full.txt"); // XXX am I allowed to do this?
            return null;
        }


        String key = "G";
        voicePart = 'B'; // obviously it's a cheat that we secretly change this to bass

        List<Integer> chords = new ArrayList<Integer>();
        List<String> lyrics = new ArrayList<String>();
        List<List<Integer>> rhythm = new ArrayList<List<Integer>>();
        List<List<Integer>> melody = new ArrayList<List<Integer>>();

        List<Integer> refrainChords = Chords.getChords();
        int verseIndex = 0;
        List<List<String>> allLyrics = lyricWriter.getAllLyrics(goodLine, ofy);
        List<String> refrainLyrics = allLyrics.get(verseIndex++);
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
                List<String> verseLyrics = allLyrics.get(verseIndex++);
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
