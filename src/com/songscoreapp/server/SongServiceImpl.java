package com.songscoreapp.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.songscoreapp.client.SongService;
import com.songscoreapp.server.generator.MusicGenerator;
import com.songscoreapp.server.objectify.DbLoader;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SongServiceImpl extends RemoteServiceServlet implements
        SongService {

    Objectify ofy = ObjectifyService.begin();
    MusicGenerator gen = new MusicGenerator(ofy);

    @Override
    public String songServer(String input) throws IllegalArgumentException {

        // Verify that the input is valid.
        if (input == null || input.length() < 4) {
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException(
                "Name must be at least 4 characters long");
        }

        if(input != null && input.equals("Admin: load words")) {
            DbLoader loader = new DbLoader(ofy);
            loader.loadWords("src/com/songscoreapp/server/resources/words-full.txt"); // XXX am I allowed to do this?
            return null;
        }

        // Escape data from the client to avoid cross-site script vulnerabilities.
        input = escapeHtml(input);
        String htmlString = gen.getSheetMusicFromInput(input, 'B');

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
