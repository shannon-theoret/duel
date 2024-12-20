package com.shannontheoret.duel;

import com.shannontheoret.duel.card.CardName;

public class CardDTO {
    private CardName cardName;
    private boolean isActive;

    public CardDTO(CardName cardName, boolean isActive) {
        this.cardName = cardName;
        this.isActive = isActive;
    }

    public CardName getCardName() {
        return cardName;
    }

    public void setCardName(CardName cardName) {
        this.cardName = cardName;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        isActive = isActive;
    }
}
