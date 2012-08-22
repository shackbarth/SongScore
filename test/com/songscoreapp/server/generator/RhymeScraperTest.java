package com.songscoreapp.server.generator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.Test;

public class RhymeScraperTest {

    @Test
    public void testScrape() {
        String scrape = "";
        try {
            scrape = RhymeScraper.scrape("for");

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(scrape);
        assertTrue(scrape.contains("your"));
        assertFalse(scrape.contains("back&nbsp;door"));
    }

}
