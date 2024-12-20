package com.shannontheoret.duel.card;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommercialBuildingCard extends Card {

    private final Set<Resource> tradeForOne;
    private final List<Resource> oneOfResources;
    private final Integer money;
    private final CardOrValueType moneyPerType;

    public CommercialBuildingCard(Integer age, Cost cost, Set<Resource> tradeForOne) {
        super(age, cost, 0);
        this.tradeForOne = tradeForOne;
        this.oneOfResources = new ArrayList<>();
        this.money = 0;
        this.moneyPerType = null;
    }

    public CommercialBuildingCard(Integer age, Cost cost, List<Resource> oneOfResources) {
        super(age, cost, 0);
        this.tradeForOne = new HashSet<>();
        this.oneOfResources = oneOfResources;
        this.money = 0;
        this.moneyPerType = null;
    }

    public CommercialBuildingCard(Integer age, Cost cost, Integer victoryPoints, Integer money, CardOrValueType moneyPerType) {
        super(age, cost, victoryPoints);
        this.tradeForOne = new HashSet<>();
        this.oneOfResources = new ArrayList<>();
        this.money = money;
        this.moneyPerType = moneyPerType;
    }

    @Override
    public CardOrValueType getCardType() {
        return CardOrValueType.COMMERCIAL_BUILDING;
    }

    public Set<Resource> getTradeForOne() {
        return tradeForOne;
    }

    public List<Resource> getOneOfResources() {
        return oneOfResources;
    }

    public Integer getMoney() {
        return money;
    }

    public CardOrValueType getMoneyPerType() {
        return moneyPerType;
    }
}
