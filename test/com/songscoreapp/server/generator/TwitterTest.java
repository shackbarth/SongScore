package com.songscoreapp.server.generator;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;

public class TwitterTest {
    @Test
    public void testGetTwitterSearchQueries() {
        List<String> lyrics = Twitter.getTwitterLyrics("Syria you look so swell");
        for(String lyric : lyrics) {
            System.out.println(lyric);
        }
    }

    @Test
    public void testTrimPunctuation() {
        assertEquals("Hello", Twitter.trimPunctuation("Hello."));
        assertEquals("Hello", Twitter.trimPunctuation(">2Hello."));
        assertEquals("Bye", Twitter.trimPunctuation("Bye"));
    }

    @Test
    public void testStripJunk() {
        assertEquals("Real text", Twitter.stripJunk("RT @Bob Real text #OMG"));
        assertEquals("Real #text with internal hash", Twitter.stripJunk("RT @Bob Real #text with internal hash #OMG"));

        assertEquals("Up in the early AM bout to get my stroke on", Twitter.stripJunk("Up in the early AM bout to get my stroke on #phelps #2 lol"));
        assertEquals("Winners don't smoke weed, champions do.", Twitter.stripJunk("Lmfaooo yeeeeeahhhh! RT @GanjaGarb: Winners don't smoke weed, champions do. #Phelps.... http://t.co/5K2Bc52E"));
        assertEquals("Winners don't smoke weed, champions do.", Twitter.stripJunk("“@GanjaGarb Winners don't smoke weed, champions do. #Phelps.... http://t.co/sCwbpBLq”&gt;&gt;&gt; haha"));
    }

}
