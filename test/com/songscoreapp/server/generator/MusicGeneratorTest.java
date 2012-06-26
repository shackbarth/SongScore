package com.songscoreapp.server.generator;

import org.junit.Test;

public class MusicGeneratorTest {
    @Test
    public void testMusicGenerator() {
        System.out.println(MusicGenerator.getSheetMusicFromInput("This is the good line", 'B'));
    }
}
