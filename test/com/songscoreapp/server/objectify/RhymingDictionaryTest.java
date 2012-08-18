package com.songscoreapp.server.objectify;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class RhymingDictionaryTest {

    Objectify ofy;
    RhymingDictionary rhymingDictionary;
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
        ofy = ObjectifyService.begin();
        ObjectifyService.register(Word.class);
        DbLoader dbLoader = new DbLoader(ofy);
        dbLoader.loadRhymes("src/com/songscoreapp/server/resources/rhymes.txt");
        rhymingDictionary = new RhymingDictionary(ofy);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testObjectify() {
        List<String> expectedRhymes = Arrays.asList(new String[] {"rat", "cat"});
        List<String> actualRhymes = rhymingDictionary.getRhymes("at");
        assertEquals(expectedRhymes, actualRhymes);

    }
}
