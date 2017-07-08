// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An instance the class encapsulates a single initialisation response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialisationResponse {

    private String gameId;

    private String word;

    private long guessesLeft;

    private String error;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getGuessesLeft() {
        return guessesLeft;
    }

    public void setGuessesLeft(long guessesLeft) {
        this.guessesLeft = guessesLeft;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override public int hashCode() {
        int hashCode = 31;
        hashCode = hashCode + this.gameId != null ? this.gameId.hashCode() ^ 2 : 130278;
        hashCode = hashCode + this.word != null ? this.word.hashCode() ^ 3 : 32023;
        hashCode = hashCode + (int) this.guessesLeft ^ 2;
        hashCode = hashCode + this.error != null ? this.error.hashCode() ^ 2 : 830232;
        return hashCode;
    }

    @Override public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!InitialisationResponse.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        InitialisationResponse response = (InitialisationResponse) obj;
        return this.gameId != null && this.gameId.equals(response.getGameId()) &&
            this.word != null && this.word.equals(response.getWord()) &&
            this.guessesLeft == response.getGuessesLeft() &&
            this.error != null && this.error.equals(response.getError());
    }

    @Override public String toString() {
        return new StringBuilder(this.getClass().getSimpleName()).append("[Game Id =")
            .append(this.gameId).append(", Word = ").append(this.word)
            .append(", Guesses left = ").append(this.guessesLeft).append("]").toString();
    }
}
