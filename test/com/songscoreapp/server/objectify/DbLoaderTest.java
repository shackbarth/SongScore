package com.songscoreapp.server.objectify;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        try {
            ObjectifyService.register(Word.class);
        } catch(Exception e) {

        }
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testLoadWords() {
        Objectify ofy = ObjectifyService.begin();

        DbLoader dbLoader = new DbLoader(ofy);
        boolean success = dbLoader.loadWords("war/resources/words-full.txt");
        assertTrue(success);
    }

    @Test
    public void testLoadWordsDouble() {
        Objectify ofy = ObjectifyService.begin();

        DbLoader dbLoader = new DbLoader(ofy);
        dbLoader.loadWords("war/resources/words-full.txt");
        boolean success = dbLoader.loadWords("war/resources/words-full.txt");

        assertFalse(success);
    }
}
