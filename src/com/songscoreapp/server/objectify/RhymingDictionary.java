package com.songscoreapp.server.objectify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;


public class RhymingDictionary {

    Objectify ofy;

    public RhymingDictionary(Objectify ofy) {
        this.ofy = ofy;
    }

    /**
     * What are all the words that rhyme with this word?
     *
     * @param word
     * @return A list of rhymes sorted by popularity.
     *
     */
    public List<String> getRhymes(String word) {
        return getRhymes(word, 999999);
    }
    public List<String> getRhymes(String word, int limit) {
        List<String> rhymes = new ArrayList<String>();

        Word wordInDb = ofy.query(Word.class).filter("word", word.toLowerCase()).get();
        if(wordInDb == null) {
            return null;
        }
        int rhymeId = wordInDb.rhymeId;
        Query<Word> q = ofy.query(Word.class).filter("rhymeId", rhymeId).order("popularity").limit(limit);
        for (Word rhyme: q) {
            if(!rhyme.getWord().equalsIgnoreCase(word)) {
                rhymes.add(rhyme.getWord());
            }
        }

        return rhymes;
    }

    /**
     * Of all of the leads, which are the most promising?
     *
     * @param leads
     * @param limit Don't return more than this many
     * @return
     */
    public List<String> getTopLeads(List<String> leads, int limit) {
        // quality list is {leadIndex, quality}
        List<int[]> qualityList = new ArrayList<int[]>(leads.size());
        for(int i = 0; i < leads.size(); i++) {
            qualityList.add(new int[] {i, getLineQuality(leads.get(i))});
        }
        Collections.sort(qualityList, new Comparator<int[]>() {
            @Override
            public int compare(int[] a1, int[] a2) {
                return a2[1] - a1[1];
            }
        });

        List<String> topLeads = new ArrayList<String>();
        for(int i = 0; i < limit && i < leads.size(); i++) {
            int leadIndex = qualityList.get(i)[0];
            int quality = qualityList.get(i)[1];
            if(quality > 0) {
                //Util.log("Adding lead " + leads.get(leadIndex) + " of quality " + quality);
                topLeads.add(leads.get(leadIndex));
            }
        }
        return topLeads;
    }

    List<String> lousyWords = Arrays.asList(new String[] {"in", "a", "the", "to", "for", "on"});

    /**
     * Rates how good this line is as far as it's rhyming potential. Basically,
     * how many words does the last word rhyme with?
     *
     * TODO: better to rate based on the popularity of the rhymes. This would
     * require a slightly different DbLoaderPrep algorithm; we'd want to
     * cache not just the rhymecount but the popularity-weighted rhymecount.
     *
     * @param line
     * @return
     */
    public int getLineQuality(String line) {
        String[] words = line.split("\\s+");
        String lastWord = words[words.length - 1];
        return getWordQuality(lastWord);
    }
    public int getWordQuality(String word) {
        if(lousyWords.indexOf(word) >= 0) {
            return -1;
        }
        int rhymeCount = 0;
        Query<Word> q = ofy.query(Word.class).filter("word", word);
        for (Word wordFromDb : q) {
            rhymeCount += wordFromDb.getRhymeCount();
        }
        return rhymeCount;
    }
}
