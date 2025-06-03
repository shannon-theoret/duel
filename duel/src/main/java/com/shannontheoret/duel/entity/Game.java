package com.shannontheoret.duel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shannontheoret.duel.*;
import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.card.CardOrValueType;
import com.shannontheoret.duel.exceptions.InvalidMoveException;
import com.shannontheoret.duel.utility.PyramidUtility;
import jakarta.persistence.*;

import java.util.*;

@Entity(name = "game")
public class Game {

    @Id
    @Column(nullable = false, length = 8, updatable = false)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStep step;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer age = 1;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer currentPlayerNumber = 1;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "player_1_id", nullable = false, unique = true)
    private Player player1;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "player_2_id", nullable = false, unique = true)
    private Player player2;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "military_id", nullable = false, unique = true)
    private Military military;

    @ElementCollection
    @CollectionTable(name="pyramid", joinColumns = @JoinColumn(name = "game_code", referencedColumnName = "code"))
    @MapKeyColumn(name="pyramid_index", nullable = false)
    @Column(name="card", nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Map<Integer, CardName> pyramid = new HashMap<>();

    @ElementCollection
    @CollectionTable(
            name = "discarded_cards",
            joinColumns = @JoinColumn(
                    name = "game_code",
                    referencedColumnName = "code",
                    foreignKey = @ForeignKey(name = "FK_discarded_cards_game")
            )
    )
    @Column(name = "card_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<CardName> discardedCards = EnumSet.noneOf(CardName.class);

    @ElementCollection
    @CollectionTable(
            name = "tokens_available",
            joinColumns = @JoinColumn(
                    name = "game_code",
                    referencedColumnName = "code",
                    foreignKey = @ForeignKey(name = "FK_tokens_available_game")
            )
    )
    @Column(name = "token", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<ProgressToken> tokensAvailable;

    @ElementCollection
    @CollectionTable(
            name = "tokens_unavailable",
            joinColumns = @JoinColumn(
                    name = "game_code",
                    referencedColumnName = "code",
                    foreignKey = @ForeignKey(name = "FK_tokens_unavailable_game")
            )
    )
    @Column(name = "token", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<ProgressToken> tokensUnavailable;

    @ElementCollection
    @CollectionTable(
            name = "tokens_from_unavailable",
            joinColumns = @JoinColumn(
                    name = "game_code",
                    referencedColumnName = "code",
                    foreignKey = @ForeignKey(name = "FK_tokens_from_unavailable_game")
            )
    )
    @Column(name = "token", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<ProgressToken> tokensFromUnavailable = EnumSet.noneOf(ProgressToken.class);

    @ElementCollection
    @CollectionTable(
            name = "wonders_available",
            joinColumns = @JoinColumn(
                    name = "game_code",
                    referencedColumnName = "code",
                    foreignKey = @ForeignKey(name = "FK_wonders_available_game")
            )
    )
    @Column(name = "wonder", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Wonder> wondersAvailable;

    @ElementCollection
    @CollectionTable(
            name = "wonders_unavailable",
            joinColumns = @JoinColumn(
                    name = "game_code",
                    referencedColumnName = "code",
                    foreignKey = @ForeignKey(name = "FK_wonders_unavailable_game")
            )
    )
    @Column(name = "wonder", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Wonder> wondersUnavailable;

    public Game() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GameStep getStep() {
        return step;
    }

    public void setStep(GameStep step) {
        this.step = step;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(Integer currentPlayerNumber) {
        this.currentPlayerNumber = currentPlayerNumber;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Map<Integer, CardName> getPyramid() {
        return pyramid;
    }

    public void setPyramid(Map<Integer, CardName> pyramid) {
        this.pyramid.clear();
        if (pyramid != null) {
            this.pyramid.putAll(pyramid);
        }
    }

    public Military getMilitary() {
        return military;
    }

    public void setMilitary(Military military) {
        this.military = military;
    }

    public Set<CardName> getDiscardedCards() {
        return discardedCards;
    }

    public void setDiscardedCards(Set<CardName> discardedCards) {
        this.discardedCards = discardedCards;
    }

    public Set<ProgressToken> getTokensAvailable() {
        return tokensAvailable;
    }

    public void setTokensAvailable(Set<ProgressToken> tokensAvailable) {
        this.tokensAvailable = tokensAvailable;
    }

    public Set<ProgressToken> getTokensUnavailable() {
        return tokensUnavailable;
    }

    public void setTokensUnavailable(Set<ProgressToken> tokensUnavailable) {
        this.tokensUnavailable = tokensUnavailable;
    }

    public Set<ProgressToken> getTokensFromUnavailable() {
        return tokensFromUnavailable;
    }

    public void setTokensFromUnavailable(Set<ProgressToken> tokensFromUnavailable) {
        this.tokensFromUnavailable = tokensFromUnavailable;
    }

    public Set<Wonder> getWondersAvailable() {
        return wondersAvailable;
    }

    public void setWondersAvailable(Set<Wonder> wondersAvailable) {
        this.wondersAvailable = wondersAvailable;
    }

    public Set<Wonder> getWondersUnavailable() {
        return wondersUnavailable;
    }

    public void setWondersUnavailable(Set<Wonder> wondersUnavailable) {
        this.wondersUnavailable = wondersUnavailable;
    }

    public Map<Integer, CardDTO> getVisiblePyramid() {
        return PyramidUtility.generateVisiblePyramid(age, pyramid);
    }

    public void changeCurrentPlayer() {
        if (currentPlayerNumber == 1) {
            currentPlayerNumber = 2;
        } else {
            currentPlayerNumber = 1;
        }
    }

    public Player findActivePlayer() throws InvalidMoveException {
        if (currentPlayerNumber == 1) {
            return player1;
        } else if (currentPlayerNumber == 2) {
            return player2;
        } else {
            throw new InvalidMoveException("Not currently a player's turn");
        }
    }

    public Player findNonActivePlayer() throws InvalidMoveException {
        if (currentPlayerNumber == 1) {
            return player2;
        } else if (currentPlayerNumber == 2) {
            return player1;
        } else {
            throw new InvalidMoveException("Not currently a player's turn");
        }
    }

    public void applyMilitaryEffect() throws InvalidMoveException {
        if (military.getMilitaryPosition() > 9) {
            military.setMilitaryPosition(9);
        }
        if (military.getMilitaryPosition() < -9) {
            military.setMilitaryPosition(-9);
        }
        if (military.getMilitaryPosition() >= -8 && military.getMilitaryPosition() <= -6 && military.getLoot5Player1Available()) {
            military.setLoot5Player1Available(false);
            player1.decreaseMoneyButNotIntoNegative(5);
        } else if (military.getMilitaryPosition() >= -5 && military.getMilitaryPosition() <= -3 && military.getLoot2Player1Available()) {
            military.setLoot2Player1Available(false);
            player1.decreaseMoneyButNotIntoNegative(2);
        } else if (military.getMilitaryPosition() >= 3 && military.getMilitaryPosition() <= 5 && military.getLoot2Player2Available()) {
            military.setLoot2Player2Available(false);
            player2.decreaseMoneyButNotIntoNegative(2);
        } else if (military.getMilitaryPosition() >= 6 && military.getMilitaryPosition() <= 8 && military.getLoot5Player2Available()) {
            military.setLoot5Player2Available(false);
            player2.decreaseMoneyButNotIntoNegative(5);
        } else if (military.getMilitaryPosition() == -9 || military.getMilitaryPosition() == 9) {
            step = GameStep.GAME_END;
            findActivePlayer().setWinStatus(WinStatus.MILITARY_VICTORY);
            findNonActivePlayer().setWinStatus(WinStatus.LOST);
        }
    }
}
