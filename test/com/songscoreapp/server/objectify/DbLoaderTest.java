package com.songscoreapp.server.objectify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class DbLoaderTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        ObjectifyService.register(Word.class);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testLoadWords() {
        Objectify ofy = ObjectifyService.begin();

        DbLoader dbLoader = new DbLoader(ofy);
        dbLoader.loadWords("src/com/songscoreapp/server/resources/words-full.txt");

        DbLoaderPrep dbLoaderPrep = new DbLoaderPrep(ofy);
        dbLoaderPrep.printAllWords();

    }
}
