package com.sander.wrdcounterworker.dto;

import java.util.ArrayList;
import java.util.List;

public class Words {
    public List<Word> words = new ArrayList<>();

    //Check if the list contains a specific word
    public boolean containsByWord(String searchWord) {
        for (Word word: words) {
            if (word.getWord().equals(searchWord)) {
                return true;
            }
        }
        return false;
    }

    //Search a word from list by supplying a word to search for
    public Word findByWord(String searchWord) {
        for (Word word: words) {
            if(word.getWord().equals(searchWord)) {
                return word;
            }
        }
        return null;
    }

    //Search word from list and increase its count
    public void updateCountByWord(String searchWord) {
        for (Word word: words) {
            if(word.getWord().equals(searchWord) ) {
                words.indexOf(word);
                words.set(words.indexOf(word), new Word(word.getWord(), word.getCount()+1));
            }
        }
    }

    //Add new word to list
    public void add(Word word) {
        words.add(word);
    }
}
