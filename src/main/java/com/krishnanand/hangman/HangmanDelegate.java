// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import java.util.Scanner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Delegate implemengation that is responsible for invoking the hang man function.
 */
@Component
public class HangmanDelegate implements IHangmanDelegate {

    private final IHangmanService hangmanService;

    private static final Log LOGGER = LogFactory.getLog(Hangman.class);

    @Autowired
    public HangmanDelegate(IHangmanService hangmanService) {
        this.hangmanService = hangmanService;
    }

    /**
     * Invokes the hangman game.
     *
     * <p>The implementation is given below:
     *
     * <ul>
     *     <li><Register the hangman by email address. The email will be provided through the command
     *     line. No client side validation of will be performed.</li>
     *     <li>Use the acquired token to play hang man.</li>
     *     <li>Once the hangman game is terminated, display the output.</li>
     * </ul>
     *
     * @return value object representing the outcome of the hangman game
     */
    @Override public HangmanOutcome playHangMan() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Enter email address :  ");
        }
        String str = "";
        try (Scanner scanner = new Scanner(System.in)) {
            // Not validating email address here.
            String email = scanner.next();
            InitialisationResponse initResponse = this.hangmanService.register(email);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("The hangman puzzle question = " + initResponse.getWord());
                LOGGER.info("You have " + initResponse.getGuessesLeft() + " guesses remaining");
                LOGGER.info(
                    "Enter a single character and press return key. Type \"quit\" (without "
                        + "quotes) to " + "quit the game.");
            }
            str = scanner.next();
            CharacterSelectionResponse response = null;
            boolean isPuzzleSolved = false;
            boolean areAttemptedExhausted = false;
            boolean didUserQuit = false;
            while (!str.equals("quit")) {
                char c = str.charAt(0);
                response = this.hangmanService.playHangman(initResponse, c);
                if (LOGGER.isInfoEnabled()) {
                    if (response.getError() != null) {
                        LOGGER.info(response.getError());
                    } else {
                        LOGGER.info(response.getMsg());
                        LOGGER.info("The hangman puzzle = " + response.getWord());
                        LOGGER.info("You have " + response.getGuessesLeft() + " guesses remaining.");
                    }
                }
                if (this.hangmanService.isPuzzleSolved(response)) {
                    isPuzzleSolved = true;
                    break;
                } else if (this.hangmanService.areAttemptsExhausted(response)) {
                    areAttemptedExhausted = true;
                    break;
                }
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(
                        "Enter  a single character and press return key. Type \"quit\" (without "
                            + "quotes) to quit the game.");
                }
                str = scanner.next();
                if (str.equals("quit")) {
                    didUserQuit = true;
                    break;
                }
            }
            HangmanOutcome hm = new HangmanOutcome();
            hm.setWasPuzzleSolved(isPuzzleSolved);
            hm.setDidUserQuit(didUserQuit);
            hm.setAllAttemptsExhausted(areAttemptedExhausted);
            hm.setCharacterSelectionResponse(response);
            return hm;
        }

    }
}
