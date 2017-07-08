package com.krishnanand.hangman;// Copyright 2017 ManOf Logan. All Rights Reserved.


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * This represents the entry point for the Hangman game.
 *
 * <p>The main class invokes the delegate responsible for playing the hangman function.
 *
 */
public class Hangman {

    private ApplicationContext initApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(HangmanConfiguration.class);
        context.refresh();
        return context;
    }

    private static final Log LOGGER = LogFactory.getLog(Hangman.class);

    public static void main(String[] args) {

        // Initialise all our dependencies.
        Hangman start = new Hangman();
        ApplicationContext context = start.initApplicationContext();
        IHangmanDelegate delegate = context.getBean(IHangmanDelegate.class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initiating hangman");
        }
        HangmanOutcome ho = delegate.playHangMan();

        // True, then the response value can not be null.
        if (ho.isWasPuzzleSolved()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(
                    "The hangman puzzle has been solved for word " + ho.getCharacterSelectionResponse().getWord());
            }

        } else if (ho.isDidUserQuit()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("The user quit the hangman puzzle.");
            }

        }
    }
}
