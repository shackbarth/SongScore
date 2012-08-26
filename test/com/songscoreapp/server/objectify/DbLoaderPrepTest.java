package com.songscoreapp.server.objectify;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class DbLoaderPrepTest {
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

    // This takes hours and only needs to be one once ever
    @Ignore
    @Test
    public void testLoadRhymes() {
        Objectify ofy = ObjectifyService.begin();

        DbLoaderPrep dbLoaderPrep = new DbLoaderPrep(ofy);
        dbLoaderPrep.loadRhymes("src/com/songscoreapp/server/resources/rhymes-full.txt");
        dbLoaderPrep.assignPopularity("src/com/songscoreapp/server/resources/google-10000-english.txt");
        dbLoaderPrep.printAllWords();

        Word fetched = ofy.get(Word.class, 10);
        System.out.println(fetched.toString());

    }

    // This takes hours and only needs to be one once ever
    // Thanks https://github.com/eurekaoverdrive/google-10000-english/blob/master/google-10000-english.txt
    @Ignore
    @Test
    public void testPrintRhymesets() {
        Objectify ofy = ObjectifyService.begin();
        //ObjectifyService.register(Word.class);

        DbLoaderPrep dbLoaderPrep = new DbLoaderPrep(ofy);
        dbLoaderPrep.printRhymesets("src/com/songscoreapp/server/resources/google-10000-english.txt");

        Word fetched = ofy.query(Word.class).filter("word", "stable").get();
        assertEquals(fetched.rhymeId, -1);
    }

}
