package com.shannontheoret.duel.card;

public class CivilianBuildingCard extends Card {

    public CivilianBuildingCard(Integer age, Cost cost, Integer victoryPoints) {
        super(age, cost, victoryPoints);
    }

    @Override
    public CardOrValueType getCardType() {
        return CardOrValueType.CIVILIAN_BUILDING;
    }
}
