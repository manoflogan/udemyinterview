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
     * @param response initialisation to be for verification
     * @return value object representing the outcome of a single hangman attempt
     */
    GameStatusResponse playHangman(InitialisationResponse response);

    /**
     * Returns {@code true} if the puzzle is solved.
     */
    boolean isPuzzleSolved(GameStatusResponse response);

    /**
     * Returns {@code true} if all attempts are exhausted
     */
    boolean areAttemptsExhausted(GameStatusResponse response);

    /**
     * Finds the current game status.
     *
     * @param gameId unique Id
     * @return response upon querying
     */
    GameStatusResponse findCurrentGameStatus(String gameId);

    /**
     * Initialise the initial word in our data structure.
     */
    void loadWords();

}
