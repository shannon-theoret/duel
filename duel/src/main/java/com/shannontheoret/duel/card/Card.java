package com.shannontheoret.duel.card;

public abstract class Card {
    private final Integer age;
    private final Cost cost;
    private final Integer victoryPoints;

    public Card(Integer age, Cost cost, Integer victoryPoints) {
        this.age = age;
        this.cost = cost;
        this.victoryPoints = victoryPoints;
    }

    public Integer getAge() {
        return age;
    }

    public abstract CardOrValueType getCardType();

    public Cost getCost() {
        return cost;
    }

    public Integer getVictoryPoints() {
        return victoryPoints;
    }
}
