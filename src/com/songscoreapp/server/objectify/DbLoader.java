package com.songscoreapp.server.objectify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.googlecode.objectify.Objectify;
import com.songscoreapp.server.generator.RhymeScraper;

public class DbLoader {
    Objectify ofy;

    public DbLoader(Objectify ofy) {
        this.ofy = ofy;

    }

    public void loadRhymes(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String line;
            int rhymeId = 0;
            while((line = reader.readLine()) != null) {
                //System.out.println("loading wordset " + rhymeId);
                line = line.replaceAll("\"", "");
                String[] words = line.split(",");
                for(String word : words) {
                    // Simple create
                    Word ofyWord = new Word(word, rhymeId);
                    ofy.put(ofyWord);
                }
                rhymeId++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadPopular(String filename, int popularity) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String word;
            while((word = reader.readLine()) != null) {
                word = word.toLowerCase();

                // don't put in a word that's already in the DB
                Word wordInDb = ofy.query(Word.class).filter("word", word).get();
                if(wordInDb == null) {
                    String rhymes = RhymeScraper.scrape(word);
                    System.out.println(rhymes);
                    String[] rhymeArray = rhymes.split(",");
                    for(String rhyme : rhymeArray) {
                        Word wordToPersist = new Word(rhyme, 999);
                        ofy.put(wordToPersist);
                    }
                }

                //Word ofyWord = new Word(word, rhymeId);
                //ofy.put(ofyWord);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
