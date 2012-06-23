package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String chordToLetter(Integer chord) {
        switch (chord) {
            case 1: return "G";
            case 2: return "Am";
            case 3: return "Bm";
            case 4: return "C";
            case 5: return "D";
            case 6: return "Em";
            default: return null;
        }
    }

    public static String integerToLetter(Integer note) {
        switch (note) {
            //case 1: return "g";   // non-occuring
            case 2: return "A,";
            case 3: return "B,";
            case 4: return "C";
            case 5: return "D";
            case 6: return "E";
            case 7: return "F";
            case 8: return "G";
            case 9: return "A";
            case 10: return "B";
            case 11: return "c";
            case 12: return "d";
            default: return null;
        }
    }

    public static Integer getSyllableCountFromWord(String word) {
        return 1;
    }

    public static List<String> getSyllablesFromWord(String word) {
        List<String> list = new ArrayList<String>();
        int n = getSyllableCountFromWord(word);
        if (n == 1) {
        	list.add(word);
        	return list;
        }

        String[] syllables = word.replaceAll("([aeiouyAEIOUY]+)", "$1#").split("#", n);
        for (String s : syllables) {
            list.add(s.replaceAll("#", ""));
        }
        //list.remove(list.size() - 1);
        return list;
    }

    public static Integer getSyllableCountFromLine(List<String> line) {
        Integer totalSyllables = 0;
        for(String word : line) {
            totalSyllables += getSyllableCountFromWord(word);
        }
        return totalSyllables;
    }

    public static Integer getSyllableCountFromLine(String line) {
        Integer totalSyllables = 0;
        String[] words = line.split(" ");
        for(String word : words) {
            totalSyllables += getSyllableCountFromWord(word);
        }
        return totalSyllables;
    }
}
