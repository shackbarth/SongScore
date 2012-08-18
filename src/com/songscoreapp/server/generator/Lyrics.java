package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;
import com.googlecode.objectify.Objectify;
import com.songscoreapp.server.objectify.RhymingDictionary;

public class Lyrics {

    RhymingDictionary dictionary;

    public Lyrics(RhymingDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public static List<List<String>> getLyrics(String goodLine) {

        List<String> line = new ArrayList<String>();
        for(String word : goodLine.split(" ")) {
            line.add(word);
        }

        int syllablesInLine = SyllableUtil.getSyllableCountFromLine(line);

        String[] laWords = new String[] {"blah", "cha", "da", "fah", "ga", "la", "na", "sha", "tra", "what"};
        String laWord = laWords[(int) (Math.random() * laWords.length)];
        List<String> laLine = new ArrayList<String>();
        for(int i = 0; i < syllablesInLine; i++) {
            laLine.add(laWord);
        }

        List<List<String>> lyrics = new ArrayList<List<String>>();
        lyrics.add(laLine);
        lyrics.add(laLine);
        lyrics.add(laLine);
        lyrics.add(line);
        return lyrics;
    }

    public static String getSignificantWord(String phrase) {
        String[] words = phrase.split(" ");
        String significantWord = "";
        for(String word : words) {
            if(isCapitalized(word) && word.length() > significantWord.length()) {
                significantWord = word;
            } else if(!isCapitalized(significantWord) && word.length() > significantWord.length()) {
                significantWord = word;
            }
        }
        return significantWord;
    }

    public static boolean isCapitalized(String word) {
        return word != null && word.length() > 0 && Character.isUpperCase(word.charAt(0));
    }

    public void getLyricsNew(String phrase, Objectify ofy) {
        String[] words = phrase.split(" ");
        String lastWord = words[words.length - 1];
        Util.log("Let's see what rhymes with " + lastWord);
        List<String> rhymes = dictionary.getRhymes(lastWord);
        for(String rhyme : rhymes) {
            Util.log(rhyme);
        }



        String significantWord = Lyrics.getSignificantWord(phrase);
    }
}
