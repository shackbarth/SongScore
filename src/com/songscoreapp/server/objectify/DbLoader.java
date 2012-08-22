package com.songscoreapp.server.objectify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.googlecode.objectify.Objectify;

public class DbLoader {
    Objectify ofy;

    public DbLoader(Objectify ofy) {
        this.ofy = ofy;

    }

    public void loadWords(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
            String line;
            while((line = reader.readLine()) != null) {
                String[] wordSplit = line.split(",");
                String word = wordSplit[0];
                int rhymeId = Integer.parseInt(wordSplit[1]);
                int rhymeCount = Integer.parseInt(wordSplit[2]);
                int popularity = Integer.parseInt(wordSplit[3]);
                Word wordToPersist = new Word(word, rhymeId, rhymeCount, popularity);
                ofy.put(wordToPersist);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
