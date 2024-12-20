package com.shannontheoret.duel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.card.CardOrValueType;
import com.shannontheoret.duel.card.ScienceSymbol;
import com.shannontheoret.duel.card.ScientificBuildingCard;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity(name="player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "player_hand",
            joinColumns = @JoinColumn(
                    name = "player_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_player_hand_player")
            )
    )
    @Column(name = "card", nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Set<CardName> hand = new HashSet<>();

    @Column(name="money", nullable = false)
    private Integer money = 8;

    @ElementCollection
    @CollectionTable(
            name = "player_tokens",
            joinColumns = @JoinColumn(
                    name = "player_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_player_tokens_player")
            )
    )
    @Column(name = "token", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<ProgressToken> tokens = new HashSet<>();

    @Column(name="won", nullable = false)
    private Boolean won=false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CardName> getHand() {
        return hand;
    }

    public void setHand(Set<CardName> hand) {
        this.hand = hand;
    }

    public Map<CardOrValueType, Set<CardName>> getSortedHand() {
        EnumMap<CardOrValueType, Set<CardName>> sortedHand = new EnumMap<>(CardOrValueType.class);
        for (CardOrValueType cardType : CardOrValueType.getCardTypes()) {
            sortedHand.put(cardType, EnumSet.noneOf(CardName.class));
        }
        for (CardName cardName: hand) {
            sortedHand.get(cardName.getCard().getCardType()).add(cardName);
        }
        return sortedHand;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Set<ProgressToken> getTokens() {
        return tokens;
    }

    public void setTokens(Set<ProgressToken> tokens) {
        this.tokens = tokens;
    }

    public Boolean getWon() {
        return won;
    }

    public void setWon(Boolean won) {
        this.won = won;
    }

    public void decreaseMoneyButNotIntoNegative(Integer money) {
        if (money > this.money) {
            this.money = 0;
        } else {
            this.money -= money;
        }
    }

    public boolean checkScienceVictory() {
        return collectAllScienceSymbols().size() >= 6;
    }

    public boolean checkNewScienceMatch(ScienceSymbol newSymbol) {
        return collectAllScienceSymbols().contains(newSymbol);
    }

    private Set<ScienceSymbol> collectAllScienceSymbols() {
        Set<ScienceSymbol> scienceSymbols = EnumSet.noneOf(ScienceSymbol.class);
        if(tokens.contains(ProgressToken.LAW)) {
            scienceSymbols.add(ScienceSymbol.SCALE);
        }
        Set<CardName> scienceCardNames = hand.stream().filter(cardName -> cardName.getCard().getCardType().equals(CardOrValueType.SCIENTIFIC_BUILDING)).collect(Collectors.toSet());
        for(CardName scienceCardName : scienceCardNames) {
            ScientificBuildingCard scienceCard = (ScientificBuildingCard) scienceCardName.getCard();
            scienceSymbols.add(scienceCard.getScienceSymbol());
        }
        return scienceSymbols;
    }
}
