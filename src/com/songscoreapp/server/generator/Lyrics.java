package com.songscoreapp.server.generator;

import java.util.ArrayList;

public class Lyrics {

    public static ArrayList<ArrayList<String>> getLyrics(String goodLine) {

        ArrayList<String> line = new ArrayList<String>();
        for(String word : goodLine.split(" ")) {
            line.add(word);
        }

        ArrayList<ArrayList<String>> lyrics = new ArrayList<ArrayList<String>>();
        lyrics.add(line);
        lyrics.add(line);
        lyrics.add(line);
        lyrics.add(line);
        return lyrics;
    }

}
