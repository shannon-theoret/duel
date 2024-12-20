package com.shannontheoret.duel.utility;

public class PyramidProperty {
    public final boolean alwaysVisible;
    public final Integer leftIndexEmptyToBeActive;
    public final Integer rightIndexEmptyToBeActive;

    public PyramidProperty(boolean alwaysVisible, Integer leftIndexEmptyToBeActive, Integer rightIndexEmptyToBeActive) {
        this.alwaysVisible = alwaysVisible;
        this.leftIndexEmptyToBeActive = leftIndexEmptyToBeActive;
        this.rightIndexEmptyToBeActive = rightIndexEmptyToBeActive;
    }

    public PyramidProperty() {
        this.alwaysVisible = true;
        this.leftIndexEmptyToBeActive = -1;
        this.rightIndexEmptyToBeActive = -1;
    }

    public boolean isAlwaysVisible() {
        return alwaysVisible;
    }

    public Integer getLeftIndexEmptyToBeActive() {
        return leftIndexEmptyToBeActive;
    }

    public Integer getRightIndexEmptyToBeActive() {
        return rightIndexEmptyToBeActive;
    }
}
