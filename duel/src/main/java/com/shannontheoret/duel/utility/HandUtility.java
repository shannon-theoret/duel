package com.shannontheoret.duel.utility;

import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.card.CardOrValueType;

import java.util.Set;

public class HandUtility {
    public static Integer countNumberOfCardType(CardOrValueType type, Set<CardName> hand) {
        return (int) hand.stream().filter(cardName -> cardName.getCard().getCardType() == type).count();
    }
}
