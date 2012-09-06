package com.songscoreapp.server.generator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.songscoreapp.server.objectify.Word;

public class MusicGeneratorTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    Objectify ofy;

    @Before
    public void setUp() {
        helper.setUp();
        ofy = ObjectifyService.begin();
        try {
            ObjectifyService.register(Word.class);
        } catch(IllegalArgumentException e) {
        }
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testMusicGenerator() {
        //DbLoader dbLoader = new DbLoader(ofy);
        //dbLoader.loadWords("src/com/songscoreapp/server/resources/words-full.txt");

        MusicGenerator gen = new MusicGenerator();
        System.out.println(gen.getSheetMusicFromInput("This is the good line", 'B'));
    }
}
