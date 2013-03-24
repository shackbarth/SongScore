package com.songscoreapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface SongServiceAsync {
    void songServer(String input, AsyncCallback<String> callback)
            throws IllegalArgumentException;
    void containsBadWord(String line, AsyncCallback<Boolean> callback) throws IllegalArgumentException;
}
