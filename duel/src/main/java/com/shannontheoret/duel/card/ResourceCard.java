package com.shannontheoret.duel.card;

import com.shannontheoret.duel.card.Card;
import com.shannontheoret.duel.card.Cost;
import com.shannontheoret.duel.card.Resource;

import java.util.List;

public abstract class ResourceCard extends Card {
    private final List<Resource> resourcesProduced;

    public ResourceCard(Integer age, Cost cost, List<Resource> resourcesProduced) {
            super(age, cost, 0);
            this.resourcesProduced = resourcesProduced;
        }

    public List<Resource> getResourcesProduced() {
        return resourcesProduced;
    }
}
