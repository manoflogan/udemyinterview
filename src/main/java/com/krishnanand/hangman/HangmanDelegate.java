// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Delegate implemengation that is responsible for invoking the hang man function.
 */
@Component
public class HangmanDelegate implements IHangmanDelegate {

    private final IHangmanService hangmanService;

    @Autowired
    public HangmanDelegate(IHangmanService hangmanService) {
        this.hangmanService = hangmanService;
    }

    /**
     * Invokes the hangman game.
     *
     * @return value object representing the outcome of the hangman game
     */
    @Override public HangmanOutcome playHangMan() {
        System.out.print("Enter email address :  ");
        String str = "";
        try (Scanner scanner = new Scanner(System.in)) {
            // Not validating email address here.
            String email = scanner.next();
            InitialisationResponse initResponse = this.hangmanService.register(email);
            System.out.println(
                "Enter characters and press return key. Type \"quit\" (without quotes) to "
                    + "quit the game.");
            str = scanner.next();
            CharacterSelectionResponse response = null;
            boolean isPuzzleSolved = false;
            boolean areAttemptedExhausted = false;
            boolean didUserQuit = false;
            while (!str.equals("quit")) {
                char c = str.charAt(0);
                response = this.hangmanService.playHangman(initResponse, c);
                if (this.hangmanService.isPuzzleSolved(response)) {
                    isPuzzleSolved = true;
                    break;
                } else if (this.hangmanService.areAttemptsExhausted(response)) {
                    areAttemptedExhausted = true;
                    break;
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
