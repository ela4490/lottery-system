package com.bynder.lottery.util;

public class BallotGenerator {

    /**
     * a simple algorithm to generate unique codes per call in the format:
     *   prefix-System.nanoTime()-postfix
     *
     * the nanosecond obtained from System.nanoTime() will itself be split by '-'.
     * sample return value: 1-4222-4112-5289-26-174639
     */
    public static String generate(int prefix, int postfix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        long time = System.nanoTime();
        while (time > 0) {
            sb.append("-").append(time % 10000);
            time = time / 10000;
        }
        sb.append("-").append(postfix);
        return sb.toString();
    }
}
