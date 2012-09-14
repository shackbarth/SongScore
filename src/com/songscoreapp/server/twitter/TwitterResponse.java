package com.songscoreapp.server.twitter;

import java.util.List;

/**
 * Utility class for GSON
 *
 * @author shackbarth
 *
 */
public class TwitterResponse {
    List<TwitterResponseTweet> results;

    public List<TwitterResponseTweet> getResults() {
        return results;
    }

    public void setResults(List<TwitterResponseTweet> results) {
        this.results = results;
    }

}
