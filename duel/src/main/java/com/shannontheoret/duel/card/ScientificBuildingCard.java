package com.shannontheoret.duel.card;

public class ScientificBuildingCard extends Card {

    private ScienceSymbol scienceSymbol;

    public ScientificBuildingCard(Integer age, Cost cost, Integer victoryPoints, ScienceSymbol scienceSymbol) {
        super(age, cost, victoryPoints);
        this.scienceSymbol = scienceSymbol;
    }

    @Override
    public CardOrValueType getCardType() {
        return CardOrValueType.SCIENTIFIC_BUILDING;
    }

    public ScienceSymbol getScienceSymbol() {
        return scienceSymbol;
    }
}
