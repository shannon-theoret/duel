package com.shannontheoret.duel;

public enum ProgressToken {
    AGRICULTURE("Agriculture"),
    ARCHITECTURE("Architecture"),
    ECONOMY("Economy"),
    LAW("Law"),
    MASONRY("Masonry"),
    MATHEMATICS("Mathematics"),
    PHILOSOPHY("Philosophy"),
    STRATEGY("Strategy"),
    THEOLOGY("Theology"),
    URBANISM("Urbanism");

    private final String tokenTitle;

    ProgressToken(String tokenTitle) {
        this.tokenTitle = tokenTitle;
    }

    public String getTokenTitle() {
        return tokenTitle;
    }
}