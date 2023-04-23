package com.lottery.exception;

public class NoBallotSubmittedException extends RuntimeException {
    public NoBallotSubmittedException(String message) {
        super(message);
    }
}
