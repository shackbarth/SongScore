package com.songscoreapp.server.objectify;

import javax.persistence.Id;

public class Word {
    @Id Long id;
    String word;
    int rhymeId;
    int rhymeCount;
    int popularity;

    private Word() {}

    public Word(String word) {
        this.word = word;
    }

    public Word(String word, int rhymeId, int rhymeCount, int popularity) {
        this.word = word;
        this.rhymeId = rhymeId;
        this.rhymeCount = rhymeCount;
        this.popularity = popularity;
    }

    public Word(String word, int rhymeId, int rhymeCount) {
        this.word = word;
        this.rhymeId = rhymeId;
        this.rhymeCount = rhymeCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getRhymeId() {
        return rhymeId;
    }

    public void setRhymeId(int rhymeId) {
        this.rhymeId = rhymeId;
    }

    public int getRhymeCount() {
        return rhymeCount;
    }

    public void setRhymeCount(int rhymeCount) {
        this.rhymeCount = rhymeCount;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }


}
