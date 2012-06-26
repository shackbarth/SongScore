package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;

public class Lyrics {

    public static List<List<String>> getLyrics(String goodLine) {

        List<String> line = new ArrayList<String>();
        for(String word : goodLine.split(" ")) {
            line.add(word);
        }

        int syllablesInLine = SyllableUtil.getSyllableCountFromLine(line);
        List<String> laLine = new ArrayList<String>();
        for(int i = 0; i < syllablesInLine; i++) {
            laLine.add("la");
        }

        List<List<String>> lyrics = new ArrayList<List<String>>();
        lyrics.add(laLine);
        lyrics.add(laLine);
        lyrics.add(laLine);
        lyrics.add(line);
        return lyrics;
    }

}
