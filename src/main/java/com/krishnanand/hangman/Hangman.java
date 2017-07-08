package com.krishnanand.hangman;// Copyright 2017 ManOf Logan. All Rights Reserved.


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * This represents the entry point for the Hangman game.
 *
 * <p>The instance of this class accepts character by character until user types "quit".The
 * implementation is given below:
 *
 * <ul>
 *     <li><Register the hangman by email address. The email will be provided through the command
 *     line. No client side validation of will be performed.</li>
 *     <li>The registration process issues a token that will be used to identify the email
 *     address.</li>
 *     <li>The token will be used to send a character to the backend.</li>
 * </ul>
 *
 *
 *
 */
public class Hangman {

    ApplicationContext initApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(HangmanConfiguration.class);
        context.refresh();
        return context;
    }

    public static void main(String[] args) {

        // Initialise all our dependencies.
        Hangman start = new Hangman();
        ApplicationContext context = start.initApplicationContext();
        IHangmanDelegate delegate = context.getBean(IHangmanDelegate.class);
        HangmanOutcome ho = delegate.playHangMan();

        // True, then the response value can not be null.
        if (ho.isWasPuzzleSolved()) {
            System.out.println(
                "The hangman puzzle has been solved for word " + ho.getCharacterSelectionResponse().getWord());
        } else if (ho.isAllAttemptsExhausted()) {
            // This has the appropriate error message.
            System.out.println("The hangman puzzle was not solved for word " + ho.getCharacterSelectionResponse().getWord());
        } else if (ho.isDidUserQuit()) {
            System.out.println("The user quit the hangman puzzle.");

        }


    }
}
