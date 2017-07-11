// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

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

    private static final Log LOGGER = LogFactory.getLog(HangmanDelegate.class);

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

        // Not validating email address here.
        InitialisationResponse initResponse = this.hangmanService.register("test@yahoo.com");
        if (initResponse == null) {
            throw new IllegalStateException("no initialisation repsonse received");
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("The hangman puzzle question = " + initResponse.getWord());
            LOGGER.info("You have " + initResponse.getGuessesLeft() + " guesses remaining");
        }

        GameStatusResponse response = this.hangmanService.playHangman(initResponse);
        HangmanOutcome hm = new HangmanOutcome();
        hm.setWasPuzzleSolved(this.hangmanService.isPuzzleSolved(response));
        hm.setAllAttemptsExhausted(this.hangmanService.areAttemptsExhausted(response));
        hm.setCharacterSelectionResponse(response);
        return hm;


    }
}
