package com.shannontheoret.duel.card;

import java.util.List;

public class RawMaterialCard extends ResourceCard {

    public RawMaterialCard(Integer age, Cost cost, List<Resource> resourcesProduced) {
        super(age, cost, resourcesProduced);
    }

    @Override
    public CardOrValueType getCardType() {
        return CardOrValueType.RAW_MATERIAL;
    }
}
