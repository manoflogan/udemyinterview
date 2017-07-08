// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

/**
 * Delegate stragegy definition that is responsible for invoking the hangman implementation.
 */
public interface IHangmanDelegate {

    /**
     * Calls the hangman puzzle with the input from the input stream.
     */
    HangmanOutcome playHangMan();
}
