package com.songscoreapp.server.generator;


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

}
