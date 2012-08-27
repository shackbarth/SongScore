package com.songscoreapp.server.twitter;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;

public class TwitterAPI {

    public static List<String> getTweetsFromQuery(String query) throws IOException, MalformedJsonException {
        String url = getFormattedTwitterSearch(query);
        String json = queryTwitter(url);
        return TwitterAPI.parseTwitterJson(json);
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

    public static List<String> parseTwitterJson(String json) throws EOFException {
        TwitterResponse twitterResponse = new Gson().fromJson(json, TwitterResponse.class);

        List<String> lines = new ArrayList<String>();
        for(TwitterResponseTweet result : twitterResponse.getResults()) {
            lines.add(result.getText());
        }
        return lines;
    }

}
