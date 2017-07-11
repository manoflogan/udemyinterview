// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

/**
 * Stategy definition used to management of all words from a repository.
 */
public interface IWordService {

    static class Node {

        private Node[] children=new Node[26];

        public Node[] getChildren() {
            return children;
        }
    }

    /**
     * Loads sample words from a resource.
     */
    void loadWordsFromFile();

    /**
     * Returns 2 array for each occurrence of a letter.
     *
     * @param word word as string
     * @return integer array
     */
    int[] getWordCountForWordLength(String word);

    /**
     * Search a string for all possible combination of letters.
     *
     * @param str string to guess
     * @param charCountForGuesses array to hold the guesses
     */
    public void search(final String str, int[] charCountForGuesses);

}
