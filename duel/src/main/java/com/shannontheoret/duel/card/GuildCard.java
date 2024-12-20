package com.shannontheoret.duel.card;

public class GuildCard extends Card {
    private final CardOrValueType cardOrValueTypeForVictoryPoints;
    private final Integer numberOfValueTypeRequired;
    private final Integer victoryPointsPerValueType;

    private final Integer moneyPerValueType;

    public GuildCard(Integer age, Cost cost, CardOrValueType cardOrValueTypeForVictoryPoints, Integer numberOfValueTypeRequired, Integer victoryPointsPerValueType, Integer moneyPerValueType) {
        super(age, cost, 0);
        this.cardOrValueTypeForVictoryPoints = cardOrValueTypeForVictoryPoints;
        this.numberOfValueTypeRequired = numberOfValueTypeRequired;
        this.victoryPointsPerValueType = victoryPointsPerValueType;
        this.moneyPerValueType = moneyPerValueType;
    }

    @Override
    public CardOrValueType getCardType() {
        return CardOrValueType.GUILD;
    }

    public CardOrValueType getCardOrValueTypeForVictoryPoints() {
        return cardOrValueTypeForVictoryPoints;
    }

    public Integer getMoneyPerValueType() {
        return moneyPerValueType;
    }

    public Integer getVictoryPoints(Integer numberOfValueType) {
        return (numberOfValueType/numberOfValueTypeRequired) * victoryPointsPerValueType;
    }
}
