package com.songscoreapp.server.generator;

import java.util.List;
import java.util.ArrayList;


public class Util {

    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

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

    public static void log(String s) {
        System.out.println("Log: " + s);
    }

    public static void log(String label, List<String> list) {
        for(String s : list) {
            System.out.println(label + s);
        }
    }
}
