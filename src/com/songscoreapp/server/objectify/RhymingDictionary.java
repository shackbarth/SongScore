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
        List<String> rhymes = new ArrayList<String>();

        Word wordInDb = ofy.query(Word.class).filter("word", word).get();
        if(wordInDb == null) {
            return null;
        }
        int rhymeId = wordInDb.rhymeId;
        Query<Word> q = ofy.query(Word.class).filter("rhymeId", rhymeId);
        for (Word rhyme: q) {
            rhymes.add(rhyme.word);
        }


        return rhymes;
    }
}
