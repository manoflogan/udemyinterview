// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

/**
 * Strategy definition of the service responsible for invoking the hangman API.
 */
public interface IHangmanService {

    /**
     * Registers to play hangman.
     *
     * @param email email address to be registered
     * @return value object that contains auth token
     */
    InitialisationResponse register(String email);

    /**
     * Implementation of this function will invoke multiple end points to play hangman.
     *
     * @param c character to be for verification
     * @return value object representing the outcome of a single hangman attempt
     */
    CharacterSelectionResponse playHangman(InitialisationResponse response, char c);

    /**
     * Returns {@code true} if the puzzle is solved.
     */
    boolean isPuzzleSolved(CharacterSelectionResponse response);

    /**
     * Returns {@code true} if all attempts are exhausted
     */
    boolean areAttemptsExhausted(CharacterSelectionResponse response);

}
