package com.shannontheoret.duel.exceptions;

public class StaleAIDecisionException extends RuntimeException {
    public StaleAIDecisionException(String message) {
        super(message);
    }
}
