package com.songscoreapp.server.objectify;

import java.util.ArrayList;
import java.util.List;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;


public class RhymingDictionary {

    Objectify ofy;

    public RhymingDictionary(Objectify ofy) {
        this.ofy = ofy;
    }


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
}
