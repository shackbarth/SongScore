package com.songscoreapp.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.songscoreapp.client.SongService;
import com.songscoreapp.server.generator.MusicGenerator;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SongServiceImpl extends RemoteServiceServlet implements
        SongService {

    @Override
    public String songServer(String input) throws IllegalArgumentException {
        // Verify that the input is valid.
        if (input == null || input.length() < 4) {
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException(
                "Name must be at least 4 characters long");
        }

        // Escape data from the client to avoid cross-site script vulnerabilities.
        input = escapeHtml(input);
        String htmlString = MusicGenerator.getSheetMusicFromInput(input, 'B');

        return htmlString;
    }

    /**
     * Escape an html string. Escaping data received from the client helps to
     * prevent cross-site script vulnerabilities.
     *
     * @param html the html string to escape
     * @return the escaped string
     */
    private String escapeHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
            ">", "&gt;");
    }
}
