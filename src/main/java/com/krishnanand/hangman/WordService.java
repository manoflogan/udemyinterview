// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import java.util.Scanner;
import org.springframework.stereotype.Service;

/**
 * Implements the word service.
 */
@Service
public class WordService implements IWordService {

    private Trie[] tries=new Trie[30];

    private int[][] countByLength=new int[40][26];


    /**
     * Reads from the files.
     */
    @Override
    public void loadWordsFromFile() {
        try (Scanner scanner =
                new Scanner(ClassLoader.getSystemResourceAsStream("words.txt"))) {
            while (scanner.hasNext()) {
                for(String word:scanner.nextLine().split(",")){
                    addWord(word);
                }
            }
        }
    }

    @Override
    public int[] getWordCountForWordLength(String word){
        return this.countByLength[word.length()];
    }

    public void addWord(String word){
        // Arbitary implementation that excludes words greater than 30.
        // 30 is always equal to number of tries.
        if(word == null || word.length() > 30 || !word.matches("[a-z]+")) {
            return;
        }
        int l = word.length();
        if(tries[l] == null) {
            tries[l] = new Trie();
        }

        Trie t = tries[l];
        t.addWord(word);

        for (char ch : word.toCharArray()) {
            this.countByLength[l][ch - 'a']++;
        }
    }

    @Override
    public void search(final String str, int[] charCountForGuesses){
        Trie t = this.tries[str.length()];
        if(t == null) {
            return;
        }
        t.search(t.root, str, 0, "", charCountForGuesses);
    }

    static class Trie {
        private Node root=new Node();

        /**
         * Add word to trie data structure.
         *
         * @param word word to be added
         */
        public void addWord(String word){
            Node currentNode = root;
            for(char ch : word.toCharArray()){
                if(currentNode.getChildren()[ch - 'a'] == null) {
                    currentNode.getChildren()[ch - 'a'] = new Node();
                }
                currentNode=currentNode.getChildren()[ch - 'a'];
            }
        }

        /**
         * Searches for the string in the current node.
         *
         * @param cur current node in which the string is to be searched
         * @param str string t obe searched
         * @param i index of current search
         * @param outcome outcome string operation
         * @param charCountForGuesses number of characters available for guesses
         */
        public void search(Node cur, String str, int i, String outcome, int[] charCountForGuesses) {

            if(cur == null) {
                return;
            }
            if(i == str.length()) {
                for(char ch : outcome.toCharArray()) {
                    charCountForGuesses[ch - 'a'] ++;
                }
                return;
            }
            char curChar = str.charAt(i);
            if (curChar != '_'){
                this.search(
                    cur.getChildren()[curChar - 'a'], str, i+1, outcome + curChar,
                    charCountForGuesses);
            } else{

                for(int j = 0; j < 26; j++){
                    char ch = (char) (j+'a');
                    this.search(cur.getChildren()[j], str, i+1, outcome + ch,
                        charCountForGuesses);
                }
            }
        }

    }


}
