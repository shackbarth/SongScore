package com.songscoreapp.server.objectify;

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
        try {
            ObjectifyService.register(Word.class);
        } catch(IllegalArgumentException e) {
        }
        DbLoader dbLoader = new DbLoader(ofy);
        dbLoader.loadWords("src/com/songscoreapp/server/resources/words-full.txt");
        rhymingDictionary = new RhymingDictionary(ofy);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testObjectify() {
        List<String> actualRhymes = rhymingDictionary.getRhymes("at");
        assert(actualRhymes.indexOf("rat") >= 0);
    }

    // for historical reasons this word has to be in there
    @Test
    public void testObjectifyPalin() {
        List<String> actualRhymes = rhymingDictionary.getRhymes("Palin");
        assert(actualRhymes.indexOf("mailing") >= 0);
    }
}
