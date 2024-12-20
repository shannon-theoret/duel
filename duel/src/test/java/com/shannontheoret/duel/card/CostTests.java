package com.shannontheoret.duel.card;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CostTests {

    @Test
    public void calculateTotalMonetaryCost_freeBuild() {
        Cost cost = new Cost(4, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.GLASS)), CardName.ALTER);
        Set<CardName> playerHand = new HashSet<>();
        playerHand.add(CardName.STONE_RESERVE);
        playerHand.add(CardName.ALTER);
        Set<CardName> opponentHand = new HashSet<>();

        Integer monetaryCost = cost.calculateTotalMonetaryCost(playerHand, opponentHand);
        assertEquals(0, monetaryCost);
    }

    @Test
    public void calculateTotalMonetaryCost_tradingRequired() {
        Cost cost = new Cost(2, new ArrayList<>(Arrays.asList(Resource.WOOD, Resource.PAPYRUS, Resource.GLASS)));
        Set<CardName> playerHand = new HashSet<>();
        playerHand.add(CardName.GLASSWORKS);
        playerHand.add(CardName.APOTHECARY);
        Set<CardName> opponentHand = new HashSet<>();
        opponentHand.add(CardName.LUMBER_YARD);
        opponentHand.add(CardName.THEATRE);

        Integer monetaryCost = cost.calculateTotalMonetaryCost(playerHand, opponentHand);

        assertEquals(7, monetaryCost);
    }

    @Test
    public void calculateTotalMonetaryCost_yellowTradingCard() {
        Cost cost = new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.BRICK, Resource.BRICK, Resource.STONE)));
        Set<CardName> playerHand = new HashSet<>();
        playerHand.add(CardName.STONE_PIT);
        playerHand.add(CardName.CLAY_RESERVE);
        Set<CardName> opponentHand = new HashSet<>();
        opponentHand.add(CardName.CLAY_PIT);

        Integer monetaryCost = cost.calculateTotalMonetaryCost(playerHand, opponentHand);

        assertEquals(3, monetaryCost);
    }
    /*
    @Test
    public void calculateTotalMonetaryCost_yellowOneOfAvailable() {
        Cost cost = new Cost(0, new ArrayList<>(Arrays.asList(Resource.BRICK, Resource.STONE, Resource.PAPYRUS)));
        Set<CardName> playerHand = new HashSet<>();
        playerHand.add()
    }*/
}
