package com.shannontheoret.duel.card;

public class MilitaryBuildingCard extends Card {

    private Integer militaryGain;

    public MilitaryBuildingCard(Integer age, Cost cost, Integer victoryPoints, Integer militaryGain) {
        super(age, cost, victoryPoints);
        this.militaryGain = militaryGain;
    }

    public Integer getMilitaryGain() {
        return militaryGain;
    }

    @Override
    public CardOrValueType getCardType() {
        return CardOrValueType.MILITARY_BUILDING;
    }
}
