package com.songscoreapp.server.generator;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
    public void testGetSignificantWord() {
        assertEquals("Bieber", Lyrics.getSignificantWord("Justin Bieber is my hero"));
        assertEquals("cat", Lyrics.getSignificantWord("My *cat* comes from a long line of felines."));
        assertEquals("could", Lyrics.getSignificantWord("It could be the end of the road..."));
        assertEquals("ignore", Lyrics.getSignificantWord("Well could I ignore the upper case?"));
    }

    @Test
    public void testTrimPunctuation() {
        assertEquals("Hello", Lyrics.trimPunctuation("Hello."));
        assertEquals("Hello", Lyrics.trimPunctuation(">2Hello."));
        assertEquals("Bye", Lyrics.trimPunctuation("Bye"));
    }

    @Test
    public void testTrimPhrasePunctuation() {
        assertEquals("Open Question i feel justin bieber is hot i wrote", Lyrics.trimPhrasePunctuation("Open Question: \" i feel justin bieber is hot\" i wrote"));
        assertEquals("JE NE DEMANDE QU'UN VOTE", Lyrics.trimPhrasePunctuation("JE NE DEMANDE QU'UN VOTE"));
        assertEquals("like and justin bieber comment plz vote", Lyrics.trimPhrasePunctuation("= #like and #justin #bieber = #comment plz #vote"));
    }

    @Test
    public void testLyrics() {
        DbLoader dbLoader = new DbLoader(ofy);
        dbLoader.loadWords("src/com/songscoreapp/server/resources/words-full.txt");

        RhymingDictionary dictionary = new RhymingDictionary(ofy);
        Lyrics lyricWriter = new Lyrics(dictionary);
        List<List<String>> lyrics = lyricWriter.getAllLyrics("I love Bieber so crazy", ofy);
        for(List<String> verse : lyrics) {
            Util.log(verse.toString());
        }
    }

    @Test
    public void testSplitLines() {
        List<String> expectedResult = Arrays.asList(new String[] {"la la la la", "la la la la"});
        assertEquals(expectedResult, Lyrics.splitLine("la la la la la la la la"));
    }

    @Test
    public void testSplitLinesMultisyllable() {
        List<String> expectedResult = Arrays.asList(new String[] {"la la la lala", "lala la la la ra"});
        assertEquals(expectedResult, Lyrics.splitLine("la la la lala lala la la la ra"));
    }

    @Ignore
    @Test
    public void testGroupLinesByRhyme() {
        List<String> lines = Arrays.asList(new String[] {
           "Si quieres que Justin te note",
           "We'll never stop to vote",
           "my edit quote",
           "justinbieber quote",
           "EVERYONE VOTE",
           "I can't decide what sweatpants I want more"
        });

        List<String> rhymes = Arrays.asList(new String[] {"note", "goat", "quote", "vote"});

        List<List<String>> expectedResults = new ArrayList<List<String>>();
        expectedResults.add(Arrays.asList(new String[] {
            "Si quieres que Justin te note"
        }));
        expectedResults.add(Arrays.asList(new String[] {
            "my edit quote",
            "justinbieber quote"
        }));
        expectedResults.add(Arrays.asList(new String[] {
            "We'll never stop to vote",
            "EVERYONE VOTE"
        }));
        expectedResults.add(Arrays.asList(new String[] {
            "I can't decide what sweatpants I want more"
        }));
        assertEquals(expectedResults, Lyrics.groupLinesByRhyme(lines, rhymes));
    }
}
