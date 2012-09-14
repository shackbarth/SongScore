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

    /**
     *
     * @param query Something like "#Bieber rocks"
     * @return
     * @throws IOException
     * @throws MalformedJsonException
     */
    public static List<String> getTweetsFromQuery(String query) throws IOException, MalformedJsonException {
        String url = getFormattedTwitterSearch(query);
        String json = queryTwitter(url);
        return TwitterAPI.parseTwitterJson(json);
    }

    /**
     * Where the actual twitter URL is kept
     *
     * @param query
     * @return
     */
    public static String getFormattedTwitterSearch(String query)  {
        try {
            return "http://search.twitter.com/search.json?q=" + URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // never will happen
            return null;
        }
    }

    /**
     * Connects to twitter
     *
     * @param urlString
     * @return String of JSON representing the twitter results
     * @throws IOException
     */
    public static String queryTwitter(String urlString) throws IOException {
        URLConnection twitterConnection = new URL(urlString).openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(twitterConnection.getInputStream()));
        String json = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            json = json + inputLine;
        }
        return json;
    }

    /**
     * We only really care about the contents of the tweets. Return the tweets as a List of
     * Strings and ignore the rest of the JSON string.
     *
     * @param json
     * @return
     * @throws EOFException
     */
    public static List<String> parseTwitterJson(String json) throws EOFException {
        TwitterResponse twitterResponse = new Gson().fromJson(json, TwitterResponse.class);

        List<String> lines = new ArrayList<String>();
        for(TwitterResponseTweet result : twitterResponse.getResults()) {
            lines.add(result.getText());
        }
        return lines;
    }

}
