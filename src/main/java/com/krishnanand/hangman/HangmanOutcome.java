// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

/**
 * An instance of this class encapsulates the outcome of the Hangman game.
 */
public class HangmanOutcome {

    // if {@code true}, then it implies that the puzzled was solved successfully.
    private boolean wasPuzzleSolved;

    // If {@code true}, then it implies that all attempts were exhausted before the puzzle was
    // solved.
    private boolean allAttemptsExhausted;


    private GameStatusResponse characterSelectionResponse;

    public boolean isWasPuzzleSolved() {
        return wasPuzzleSolved;
    }

    public void setWasPuzzleSolved(boolean wasPuzzleSolved) {
        this.wasPuzzleSolved = wasPuzzleSolved;
    }

    public boolean isAllAttemptsExhausted() {
        return allAttemptsExhausted;
    }

    public void setAllAttemptsExhausted(boolean allAttemptsExhausted) {
        this.allAttemptsExhausted = allAttemptsExhausted;
    }

    public GameStatusResponse getCharacterSelectionResponse() {
        return characterSelectionResponse;
    }

    public void setCharacterSelectionResponse(
        GameStatusResponse characterSelectionResponse) {
        this.characterSelectionResponse = characterSelectionResponse;
    }

    @Override public int hashCode() {
        int hashCode = 31;
        hashCode = this.wasPuzzleSolved ? 1032: 8082 + hashCode;
        hashCode = this.allAttemptsExhausted ? 102303: 3223 + hashCode;
        return hashCode;
    }

    @Override public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!HangmanOutcome.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        HangmanOutcome hm = (HangmanOutcome) obj;
        return  this.wasPuzzleSolved == hm.isWasPuzzleSolved() &&
            this.allAttemptsExhausted == hm.isAllAttemptsExhausted();
    }

    @Override public String toString() {
        return new StringBuilder(this.getClass().getName()).append("[Was Puzzle Solved = ")
            .append(this.wasPuzzleSolved).append(", All Attempts Exhausted = ")
            .append(this.allAttemptsExhausted)
            .append("]").toString();
    }
}
