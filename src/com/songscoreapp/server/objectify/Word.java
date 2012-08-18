package com.songscoreapp.server.objectify;

import javax.persistence.Id;

public class Word {
    @Id Long id;
    String word;
    int rhymeId;
    int popularity;

    private Word() {}

    public Word(String word, int rhymeId)
    {
        this.word = word;
        this.rhymeId = rhymeId;
    }
}
