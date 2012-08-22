package com.songscoreapp.server.objectify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.songscoreapp.server.generator.RhymeScraper;

public class DbLoaderPrep {

    // This is an iffy technique TBD
    final static boolean mergeTransitive = false;
    Objectify ofy;

    public DbLoaderPrep(Objectify ofy) {
        this.ofy = ofy;

    }

    public void printAllWords() {
        Query<Word> q = ofy.query(Word.class);
        for (Word word: q) {
            System.out.println(word.word + "," +
                    word.rhymeId + "," +
                    word.rhymeCount + "," +
                    ((word.popularity == 0) ? 99999 : word.popularity));
        }
    }

    public void loadRhymes(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String line;
            int rhymeId = 0;
            List<Integer[]> mergeList = new ArrayList<Integer[]>();

            while((line = reader.readLine()) != null) {
                if(rhymeId % 100 == 0) {
                    System.out.println("loading wordset " + rhymeId);
                }
                line = line.replaceAll("\"", "");
                String[] words = line.split(",");
                int rhymeCount = words.length;
                for(String word : words) {
                    // Simple create
                    //Word wordInDb = ofy.query(Word.class).filter("word", word).get();
                    //if(wordInDb == null) {
                        // Allow a word to be in the table twice with two different rhymeIds if
                        // that's what RhymeZone gives us
                        Word ofyWord = new Word(word, rhymeId, rhymeCount);
                        ofy.put(ofyWord);
                    //} else if(mergeTransitive) {
                    //    System.out.println("Strange: " + word + " is already in database ["
                    //            + rhymeId + " " + wordInDb.rhymeId + "]");
                    //    mergeList.add(new Integer[] {rhymeId, wordInDb.rhymeId});
                    //}
                }
                rhymeId++;
            }

            if(mergeTransitive) {
                for(Integer[] mergeItem : mergeList) {
                    int mergeFrom = mergeItem[0];
                    int mergeTo = mergeItem[1];

                    Query<Word> q = ofy.query(Word.class).filter("rhymeId", mergeFrom);
                    for (Word wordToMerge: q) {
                        System.out.println("Merging " + wordToMerge.getWord() + " into rhyme " + mergeTo);
                        wordToMerge.setRhymeId(mergeTo);
                        ofy.put(wordToMerge);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void assignPopularity(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String word;
            int popularity = 1;
            while((word = reader.readLine()) != null) {
                word = word.toLowerCase().trim();

                // We allow for multiple instances of a word if its in more than one rhymeset
                Query<Word> q = ofy.query(Word.class).filter("word", word);
                for (Word wordToUpdate: q) {
                    wordToUpdate.setPopularity(popularity);
                    ofy.put(wordToUpdate);
                }
                popularity++;
                if(popularity % 100 == 0) {
                    System.out.println(popularity);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *
     *
     * @param filename
     * @param popularity
     */
    public void printRhymesets(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String word;
            while((word = reader.readLine()) != null) {
                word = word.toLowerCase().trim();

                // don't put in a word that's already in the DB
                Word wordInDb = ofy.query(Word.class).filter("word", word).get();
                if(wordInDb == null) {
                    String rhymes = RhymeScraper.scrape(word);
                    if(rhymes != null && rhymes.length() > 0) {
                        System.out.println(rhymes);
                    }
                    String[] rhymeArray = rhymes.split(",");
                    for(String rhyme : rhymeArray) {
                        Word wordToPersist = new Word(rhyme);
                        ofy.put(wordToPersist);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
