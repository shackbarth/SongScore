package com.songscoreapp.server.twitter;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class TwitterUtilTest {
    /*
    @Test
    public void testGetTwitterSearchQueries() {
        List<String> rhymes =
                Arrays.asList(new String[] {"note", "quote", "vote",
                        "wrote", "remote", "boat", "promote", "throat", "coat", "float"});
        List<String> lyrics = Twitter.getTwitterFragments("Justin Bieber is a goat", rhymes);
        for(String lyric : lyrics) {
            Util.log(lyric);
        }
    }
    */

    @Test
    public void testIsUsableLine() {
        assertEquals(true, TwitterUtil.isUsableLine("A good line as far as I can tell", true));
        assertEquals(false, TwitterUtil.isUsableLine("I want!! #venus #flytrap #carnivore #plants #botany #traps #cool #crazy #uws #manhattan @ Academy Floral Co", true));
        assertEquals(false, TwitterUtil.isUsableLine("Too short", true));
        assertEquals(false, TwitterUtil.isUsableLine("", true));
        assertEquals(false, TwitterUtil.isUsableLine("A bad line has https://bit.ly/24gj4", true));
    }

    @Test
    public void testStripJunk() {
        assertEquals("Real text", TwitterUtil.stripJunk("RT @Bob Real text #OMG"));
        assertEquals("Real #text with internal hash", TwitterUtil.stripJunk("RT @Bob Real #text with internal hash #OMG"));

        assertEquals("Up in the early AM bout to get my stroke on", TwitterUtil.stripJunk("Up in the early AM bout to get my stroke on #phelps #2 lol"));
        assertEquals("Winners don't smoke weed, champions do.", TwitterUtil.stripJunk("Lmfaooo yeeeeeahhhh! RT @GanjaGarb: Winners don't smoke weed, champions do. #Phelps.... http://t.co/5K2Bc52E"));
        assertEquals("Winners don't smoke weed, champions do.", TwitterUtil.stripJunk("“@GanjaGarb Winners don't smoke weed, champions do. #Phelps.... http://t.co/sCwbpBLq”&gt;&gt;&gt; haha"));

        assertEquals("excuse moi, mais tu peux voté s'il te plait? :).", TwitterUtil.stripJunk("RT @bull_justinbull: #bieber RT @Fuuuuuckement: @Manonlecitron  excuse moi, mais tu peux voté s'il te plait? :). http://t.co/B1cC1jwX"));
        assertEquals("Vote for a hot canadian that sings amazing! &lt;3! best pop artist! youu knowwww!!!!!", TwitterUtil.stripJunk("Vote for a hot canadian that sings amazing! &lt;3! best pop artist! youu knowwww!!!!! @justinbieber"));

        // tons of hashes in the middle means ignore the end
        //assertEquals("Also vote @justinbieber for best pop video!", Twitter.stripJunk("Also vote @justinbieber for best pop video! #boyfriend #VoteforBoyfriend #believe #beliebers #bieber VOTE VOTE VOTE last day!"));

        assertEquals("I can't decide what sweatpants I want more", TwitterUtil.stripJunk("RT @JBieberLilTwist: I can't decide what sweatpants I want more #helpme #justinbieber #sweatpants #vote #tellme #bieber #clothes  http://t.co/fXq3GHSv"));
    }

    @Test
    public void testStripJunkShort() {
        assertEquals("Soldier", TwitterUtil.stripJunk("Soldier"));
    }

    @Test
    public void testChopLines() {
        String inputLine = "All though it's my birthday, I don't care that I'm spending it in the ICU. I only care that she is alive.";
        List<String> expectedLines = Arrays.asList(new String[] {
                "All though it's my birthday,",
                "I don't care that I'm spending it in the ICU.",
                "I only care that she is alive."
        });
        assertEquals(expectedLines, TwitterUtil.chopLine(inputLine));
    }

    @Test
    public void testChopLines2() {
        String inputLine = "Is anyone good with little children. I need munga munga munga munga munga help.";
        List<String> expectedLines = Arrays.asList(new String[] {
                "Is anyone good with little children.",
                "I need munga munga munga munga munga help."
        });
        assertEquals(expectedLines, TwitterUtil.chopLine(inputLine));
    }

    @Test
    public void testChopLinesNoChop() {
        String inputLine = "Yazid, rocking his fresh new cut";
        List<String> expectedLines = Arrays.asList(new String[] {
                "Yazid, rocking his fresh new cut"
        });
        assertEquals(expectedLines, TwitterUtil.chopLine(inputLine));
    }

    @Test
    public void testChopLinesNoPunctuation() {
        String inputLine = "Yazid rocking his fresh new cut Yazid rocking his fresh new cut Yazid rocking his fresh new cut";
        List<String> expectedLines = Arrays.asList(new String[] {
                "Yazid rocking his fresh new cut Yazid rocking his fresh new cut Yazid rocking his fresh new cut"
        });
        assertEquals(expectedLines, TwitterUtil.chopLine(inputLine));
    }

    @Test
    public void testChopLinesNotTooShort() {
        String inputLine = "My cousin is a great guy he is crispy, smart, athletic, funny, weird, merlin, blah";
        List<String> expectedLines = Arrays.asList(new String[] {
                "My cousin is a great guy he is crispy, smart, athletic, funny, weird,",
                "merlin, blah"
        });
        assertEquals(expectedLines, TwitterUtil.chopLine(inputLine));
    }

    @Test
    public void testRhymechopLine() {
        String inputLine = "My cousin is a great guy he is crispy, smart, athletic, funny, weird, merlin, blah";
        List<String> rhymes = Arrays.asList(new String[] {"boat", "rote", "moat", "float"});
        assertEquals("Go put your boat!", TwitterUtil.rhymechopLine("Go put your boat! in a moat", rhymes));
    }


    @Test
    public void testIndexOfPunctuation() {
        assertEquals(3, TwitterUtil.indexOfPunctuation("abc. def! hij;"));
        assertEquals(-1, TwitterUtil.indexOfPunctuation("abc def hij"));
        assertEquals(0, TwitterUtil.indexOfPunctuation(",bc def hij"));
    }
}
