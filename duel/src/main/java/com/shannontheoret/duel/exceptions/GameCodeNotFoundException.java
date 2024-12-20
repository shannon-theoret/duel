package com.shannontheoret.duel.exceptions;

public class GameCodeNotFoundException extends Exception {

    public GameCodeNotFoundException(String gameCode) {
        super("Invalid game code: " + gameCode);
    }
}
