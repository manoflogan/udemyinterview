// Copyright 2017 ManOf Logan. All Rights Reserved.
package com.krishnanand.hangman;

/**
 * Thrown when any exception is thrown related to a service.
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String cause, Throwable t) {
        super("Error caused by " + cause, t);
    }

    public ServiceException(String cause) {
        super("Error caused by " + cause);
    }
}
