package com.bynder.lottery.exception;

public class FinishedLotteryException extends RuntimeException {

    public FinishedLotteryException() {
    }

    public FinishedLotteryException(String message) {
        super(message);
    }
}
