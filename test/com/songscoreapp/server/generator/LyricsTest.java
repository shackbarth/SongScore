package com.songscoreapp.server.generator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.songscoreapp.server.objectify.DbLoader;
import com.songscoreapp.server.objectify.RhymingDictionary;
import com.songscoreapp.server.objectify.Word;

public class LyricsTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    Objectify ofy;

    @Before
    public void setUp() {
        helper.setUp();
        ofy = ObjectifyService.begin();
        ObjectifyService.register(Word.class);
        DbLoader dbLoader = new DbLoader(ofy);
        dbLoader.loadWords("src/com/songscoreapp/server/resources/rhymes.txt");
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testLyrics() {
        RhymingDictionary dictionary = new RhymingDictionary(ofy);
        Lyrics lyrics = new Lyrics(dictionary);
        lyrics.getLyricsNew("I think Phelps is a cat", ofy);
    }
}
