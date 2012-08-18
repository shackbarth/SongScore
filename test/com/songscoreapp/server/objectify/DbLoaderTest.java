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
    public void testLoadRhymes() {
        Objectify ofy = ObjectifyService.begin();

        DbLoader dbLoader = new DbLoader(ofy);
        dbLoader.loadRhymes("src/com/songscoreapp/server/resources/rhymes.txt");


        Word fetched = ofy.get(Word.class, 1);
        System.out.println(fetched.word);

    }

    @Ignore
    @Test
    public void testLoadPopular() {
        Objectify ofy = ObjectifyService.begin();
        //ObjectifyService.register(Word.class);

        DbLoader dbLoader = new DbLoader(ofy);
        dbLoader.loadPopular("src/com/songscoreapp/server/resources/top1000.txt", 1000);

        Word fetched = ofy.query(Word.class).filter("word", "stable").get();
        assertEquals(fetched.rhymeId, 999);
    }
}
