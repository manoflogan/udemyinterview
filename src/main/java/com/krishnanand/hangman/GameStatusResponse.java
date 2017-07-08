// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An instance of this class represents the response for a particular character when the hangman
 * game is played.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameStatusResponse {

    private String gameId;

    private String word;

    private long guessesLeft;

    private String status;

    private String msg;

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

    public void setGuessesLeft(long guessLeft) {
        this.guessesLeft = guessLeft;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
        hashCode = hashCode + this.status != null ? this.status.hashCode() ^ 2 : 83012;
        hashCode = hashCode + this.msg != null ? this.msg.hashCode() ^ 3 : 830202;
        return hashCode;
    }

    @Override public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!GameStatusResponse.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        GameStatusResponse response = (GameStatusResponse) obj;
        return this.gameId != null && this.gameId.equals(response.getGameId()) &&
            this.word != null && this.word.equals(response.getWord()) &&
            this.guessesLeft == response.getGuessesLeft() &&
            (this.error == response.getError() || (this.error != null && this.error.equals(response
                .getError()))) &&
            this.status != null && this.status.equals(response.getStatus()) &&
            this.msg != null && this.msg.equals(response.getMsg());
    }

    @Override public String toString() {
        return new StringBuilder(this.getClass().getSimpleName()).append("[Game Id =")
            .append(this.gameId).append(", Word = ").append(this.word)
            .append(", Guesses left = ").append(this.guessesLeft)
            .append(", Status = ").append(this.status).append(", msg = ").append(this.msg)
            .append("]").toString();
    }
}
