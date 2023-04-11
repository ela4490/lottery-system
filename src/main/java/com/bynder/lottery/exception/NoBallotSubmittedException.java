package com.bynder.lottery.exception;

public class NoBallotSubmittedException extends RuntimeException {

    public NoBallotSubmittedException() {
    }

    public NoBallotSubmittedException(String message) {
        super(message);
    }

}
