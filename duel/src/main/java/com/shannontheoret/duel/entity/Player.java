package com.shannontheoret.duel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.Wonder;
import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.card.CardOrValueType;
import com.shannontheoret.duel.card.ScienceSymbol;
import com.shannontheoret.duel.card.ScientificBuildingCard;
import com.shannontheoret.duel.exceptions.InvalidMoveException;
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
    private Integer money = 7;

    @ElementCollection
    @CollectionTable(
            name="wonders",
            joinColumns = @JoinColumn(
                    name = "player_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_player_wonders_player")))
    @MapKeyColumn(name="wonder", nullable = false)
    @Column(name="age", nullable = true)
    @Enumerated(EnumType.STRING)
    private Map<Wonder, Integer> wonders = new HashMap<>();

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
        for (CardName cardName: hand) {
            CardOrValueType cardType = cardName.getCard().getCardType();
            if (cardType == CardOrValueType.RAW_MATERIAL || cardType == CardOrValueType.MANUFACTURED_GOOD) {
                cardType = CardOrValueType.RAW_MATERIAL_AND_MANUFACTURED_GOOD;
            }
            if (!sortedHand.containsKey(cardType)) {
                sortedHand.put(cardType, EnumSet.noneOf(CardName.class));
            }
            sortedHand.get(cardType).add(cardName);
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

    public Map<Wonder, Integer> getWonders() {
        return wonders;
    }

    public void setWonders(Map<Wonder, Integer> wonders) {
        this.wonders = wonders;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (!Objects.equals(id, player.id)) return false;
        if (!hand.equals(player.hand)) return false;
        if (!money.equals(player.money)) return false;
        if (!tokens.equals(player.tokens)) return false;
        return won.equals(player.won);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + hand.hashCode();
        result = 31 * result + money.hashCode();
        result = 31 * result + tokens.hashCode();
        result = 31 * result + won.hashCode();
        return result;
    }

    public void selectWonder(Wonder wonder) {
        this.wonders.put(wonder, 0);
    }

    public void purchaseWonder(Wonder wonder, Integer age) throws InvalidMoveException {
        if (!this.wonders.containsKey(wonder)) {
            throw new InvalidMoveException("Wonder not available to player.");
        }
        if (age < 1 || age > 3) {
            throw new InvalidMoveException("Age not valid.");
        }
        this.wonders.put(wonder, age);
    }

    public boolean hasWonder(Wonder wonder) {
        return this.wonders.containsKey(wonder) && this.wonders.get(wonder) > 0;
    }

        public Set<Wonder> calculateWondersConstructed() {
            return wonders.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
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
