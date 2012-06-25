package com.songscoreapp.server.generator;

import java.util.ArrayList;

public class Lyrics {

    public static ArrayList<ArrayList<String>> getLyrics(String goodLine) {

        ArrayList<String> line = new ArrayList<String>();
        for(String word : goodLine.split(" ")) {
            line.add(word);
        }

        int syllablesInLine = SyllableUtil.getSyllableCountFromLine(line);
        ArrayList<String> laLine = new ArrayList<String>();
        for(int i = 0; i < syllablesInLine; i++) {
            laLine.add("la");
        }

        ArrayList<ArrayList<String>> lyrics = new ArrayList<ArrayList<String>>();
        lyrics.add(laLine);
        lyrics.add(laLine);
        lyrics.add(laLine);
        lyrics.add(line);
        return lyrics;
    }

}
