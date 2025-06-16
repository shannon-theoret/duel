package com.shannontheoret.duel;

import com.shannontheoret.duel.card.Cost;
import com.shannontheoret.duel.card.Resource;

import java.util.ArrayList;
import java.util.List;

public enum Wonder {
    THE_APPIAN_WAY(new Cost(List.of(Resource.PAPYRUS, Resource.BRICK, Resource.BRICK, Resource.STONE, Resource.STONE)), true, 3, 3, "The Appian Way"),
    CIRCUS_MAXIMUS(new Cost(List.of(Resource.GLASS, Resource.WOOD, Resource.STONE, Resource.STONE)), false, 3, 0, 1, "Circus Maximus"),
    THE_COLOSSUS(new Cost(List.of(Resource.GLASS, Resource.BRICK, Resource.BRICK, Resource.BRICK)), false, 3, 0, 2, "The Colossus"),
    THE_GREAT_LIBRARY(new Cost(List.of(Resource.PAPYRUS, Resource.GLASS, Resource.WOOD, Resource.WOOD, Resource.WOOD)), false, 4, 0, "The Great Library"),
    THE_GREAT_LIGHTHOUSE(new Cost(List.of(Resource.PAPYRUS, Resource.PAPYRUS, Resource.STONE, Resource.WOOD)), false, 4, 0, new ArrayList<>(List.of(Resource.WOOD, Resource.STONE, Resource.BRICK)), "The Great Lighthouse"),
    THE_HANGING_GARDENS(new Cost(List.of(Resource.PAPYRUS, Resource.GLASS, Resource.WOOD, Resource.WOOD)), true, 3, 6, "The Hanging Gardens"),
    THE_MAUSOLEUM(new Cost(List.of(Resource.PAPYRUS, Resource.GLASS, Resource.GLASS, Resource.BRICK, Resource.BRICK)), false, 2, 0, "The Mausoleum"),
    PIRAEUS(new Cost(List.of(Resource.BRICK, Resource.STONE, Resource.WOOD, Resource.WOOD)), true, 2, 0, new ArrayList<>(List.of(Resource.PAPYRUS, Resource.GLASS)), "Piraeus"),
    THE_PYRAMIDS(new Cost(List.of(Resource.PAPYRUS, Resource.STONE, Resource.STONE, Resource.STONE)), false, 9, 0, "The Pyramids"),
    THE_SPHINX(new Cost(List.of(Resource.GLASS, Resource.GLASS, Resource.BRICK, Resource.STONE)), true, 6, 0, "The Sphinx"),
    THE_STATUE_OF_ZEUS(new Cost(List.of(Resource.PAPYRUS, Resource.PAPYRUS, Resource.BRICK, Resource.WOOD, Resource.STONE)), false, 3, 0, 1, "The Statue of Zeus"),
    THE_TEMPLE_OF_ARTEMIS(new Cost(List.of(Resource.PAPYRUS, Resource.GLASS, Resource.STONE, Resource.WOOD)), true, 0, 12, "The Temple of Artemis");

    private final Cost cost;
    private final boolean immediatelyPlaySecondTurn;
    private final Integer victoryPoints;
    private final Integer monetaryGain;
    private final Integer militaryGain;
    private final List<Resource> oneOfResources;
    private final String wonderName;

    Wonder(Cost cost, boolean immediatelyPlaySecondTurn, Integer victoryPoints, Integer monetaryGain, String wonderName) {
        this(cost, immediatelyPlaySecondTurn, victoryPoints, monetaryGain, 0, new ArrayList<>(), wonderName);
    }

    Wonder(Cost cost, boolean immediatelyPlaySecondTurn, Integer victoryPoints, Integer monetaryGain, Integer militaryGain, String wonderName) {
        this(cost, immediatelyPlaySecondTurn, victoryPoints, monetaryGain, militaryGain, new ArrayList<>(), wonderName);
    }

    Wonder(Cost cost, boolean immediatelyPlaySecondTurn, Integer victoryPoints, Integer monetaryGain, List<Resource> oneOfResources, String wonderName) {
        this(cost, immediatelyPlaySecondTurn, victoryPoints, monetaryGain, 0, oneOfResources, wonderName);
    }

    Wonder(Cost cost, boolean immediatelyPlaySecondTurn, Integer victoryPoints, Integer monetaryGain, Integer militaryGain, List<Resource> oneOfResources, String wonderName) {
        this.cost = cost;
        this.immediatelyPlaySecondTurn = immediatelyPlaySecondTurn;
        this.victoryPoints = victoryPoints;
        this.monetaryGain = monetaryGain;
        this.militaryGain = militaryGain;
        this.oneOfResources = oneOfResources;
        this.wonderName = wonderName;
    }

    public Cost getCost() {
        return cost;
    }

    public boolean immediatelyPlaySecondTurn() {
        return immediatelyPlaySecondTurn;
    }

    public Integer getVictoryPoints() {
        return victoryPoints;
    }

    public Integer getMonetaryGain() {
        return monetaryGain;
    }

    public Integer getMilitaryGain() {
        return militaryGain;
    }

    public List<Resource> getOneOfResources() {
        return oneOfResources;
    }

    public String getWonderName() {
        return wonderName;
    }
}
