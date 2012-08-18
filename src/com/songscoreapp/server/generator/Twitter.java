package com.songscoreapp.server.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;

public class Twitter {

    public static List<String> getTwitterLyrics(String phrase) {
        List<String> lyrics = new ArrayList<String>();

        String[] words = phrase.split(" ");
        String significantWord = Lyrics.getSignificantWord(phrase);
        String lastWord = words[words.length - 1];
        List<String> rhymes = Rhymes.getInstance().getRhymes(lastWord);
        for(String rhyme: rhymes) {
            String search = "#" + significantWord + " " + rhyme;
            String url = getFormattedTwitterSearch(search);
            String json = "";
            try {
                json = queryTwitter(url);
                List<String> lines = parseTwitterJson(json);
                for(String line : lines) {
                    String strippedLine = stripJunk(line);
                    String fragment = getFragment(strippedLine, rhyme);
                    if(fragment != null) {
                        lyrics.add(fragment);
                    }
                }

            } catch (IOException e) {
                // oh well
                e.printStackTrace();
            }

        }
        return lyrics;
    }

    public static String getFormattedTwitterSearch(String query)  {
        try {
            return "http://search.twitter.com/search.json?q=" + URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // never will happen
            return null;
        }
    }

    public static String queryTwitter(String urlString) throws IOException {
        URLConnection brickhouseConnection = new URL(urlString).openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(brickhouseConnection.getInputStream()));
        String json = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            json = json + inputLine;
        }
        return json;
    }

    public static List<String> parseTwitterJson(String json) {
        TwitterResponse twitterResponse = new Gson().fromJson(json, TwitterResponse.class);

        List<String> lines = new ArrayList<String>();
        for(TwitterResponseTweet result : twitterResponse.getResults()) {
            lines.add(result.getText());
        }
        return lines;
    }

    public static String getFragment(String line, String rhyme) {
        String fragment = "";
        String[] words = line.split(" ");
        for(String word : words) {
            fragment = fragment + word + " ";
            if(trimPunctuation(word).equalsIgnoreCase(trimPunctuation(rhyme))) {
                break;
            }
        }
        return fragment.trim();
    }

    public static String trimPunctuation(String word) {
        while(word.length() > 0 && !Character.isLetter(word.charAt(0))) {
            word = word.substring(1);
        }
        while(word.length() > 0 && !Character.isLetter(word.charAt(word.length() - 1))) {
            word = word.substring(0, word.length() - 1);
        }
        return word;
    }

    public static String stripJunk(String line) {
        List<String> words = Arrays.asList(line.split(" "));

        /**
         * Strip out retweet text
         */
        int retweetIndex = words.indexOf("RT");
        if(retweetIndex >= 0 && retweetIndex + 2 < words.size()) {
            words = words.subList(retweetIndex + 2, words.size());
        }


        /**
         * Strip off leading and trailing boguswords (and even a trailing non-hashword if it's
         * preceded by a trailing hashword
         */
        if(isBogusWord(words.get(words.size() - 2))) {
            words = words.subList(0, words.size() - 2);
        }
        while(words.size() > 0 && isBogusWord(words.get(words.size() - 1))) {
            words = words.subList(0, words.size() - 1);
        }
        while(words.size() > 0 && isBogusWord(words.get(0))) {
            words = words.subList(1, words.size());
        }

        StringBuffer strippedLine = new StringBuffer("");
        for(String word : words) {
            strippedLine.append(word + " ");
        }
        return strippedLine.toString().trim();
    }

    public static boolean isBogusWord(String word) {
        return word.startsWith("#")
                || word.startsWith("http")
                || word.contains("@");
    }

}
