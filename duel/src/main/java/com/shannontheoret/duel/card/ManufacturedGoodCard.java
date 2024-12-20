package com.shannontheoret.duel.card;

import java.util.List;

public class ManufacturedGoodCard extends ResourceCard {

    public ManufacturedGoodCard(Integer age, Cost cost, List<Resource> resourcesProduced) {
        super(age, cost, resourcesProduced);
    }

    @Override
    public CardOrValueType getCardType() {
        return CardOrValueType.MANUFACTURED_GOOD;
    }
}
