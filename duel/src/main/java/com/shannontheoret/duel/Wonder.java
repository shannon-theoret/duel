package com.shannontheoret.duel;

import com.shannontheoret.duel.card.Cost;
import com.shannontheoret.duel.card.Resource;

import java.util.ArrayList;
import java.util.List;

public enum Wonder {
    THE_APPIAN_WAY( new Cost(List.of(Resource.PAPYRUS, Resource.BRICK, Resource.BRICK, Resource.STONE, Resource.STONE)), true, 3, 3),
    CIRCUS_MAXIMUS(new Cost(List.of(Resource.GLASS, Resource.WOOD, Resource.STONE, Resource.STONE)), false, 3, 0, 1),
    THE_COLOSSUS(new Cost(List.of(Resource.GLASS, Resource.BRICK, Resource.BRICK, Resource.BRICK)), false, 3, 0, 2),
    THE_GREAT_LIBRARY(new Cost(List.of(Resource.PAPYRUS, Resource.GLASS, Resource.WOOD, Resource.WOOD, Resource.WOOD)), false, 4, 0),
    THE_GREAT_LIGHTHOUSE(new Cost(List.of(Resource.PAPYRUS, Resource.PAPYRUS, Resource.STONE, Resource.WOOD)), false, 4, 0, new ArrayList<>(List.of(Resource.WOOD, Resource.STONE, Resource.BRICK))),
    THE_HANGING_GARDENS(new Cost(List.of(Resource.PAPYRUS, Resource.GLASS, Resource.WOOD, Resource.WOOD)), true, 3, 6),
    THE_MAUSOLEUM(new Cost(List.of(Resource.PAPYRUS, Resource.GLASS, Resource.GLASS, Resource.BRICK, Resource.BRICK)), false,2, 0),
    PIRAEUS(new Cost(List.of(Resource.BRICK, Resource.STONE, Resource.WOOD, Resource.WOOD)), true, 2, 0, new ArrayList<>(List.of(Resource.PAPYRUS, Resource.GLASS))),
    THE_PYRAMIDS(new Cost(List.of(Resource.PAPYRUS, Resource.STONE, Resource.STONE, Resource.STONE)), false, 9, 0),
    THE_SPHINX(new Cost(List.of(Resource.GLASS, Resource.GLASS, Resource.BRICK, Resource.STONE)),true, 6, 0),
    THE_STATUE_OF_ZEUS(new Cost(List.of(Resource.PAPYRUS, Resource.PAPYRUS, Resource.BRICK, Resource.WOOD, Resource.STONE)), false, 3, 0, 1),
    THE_TEMPLE_OF_ARTEMIS(new Cost(List.of(Resource.PAPYRUS, Resource.GLASS, Resource.STONE, Resource.WOOD)), true, 0,12);

    private final Cost cost;
    private final boolean immediatelyPlaySecondTurn;
    private final Integer victoryPoints;
    private final Integer monetaryGain;
    private final Integer militaryGain;
    private final List<Resource> oneOfResources;

    Wonder(Cost cost, boolean immediatelyPlaySecongdTurn, Integer victoryPoints, Integer monetaryGain) {
        this.cost = cost;
        this.immediatelyPlaySecondTurn = immediatelyPlaySecongdTurn;
        this.victoryPoints = victoryPoints;
        this.monetaryGain = monetaryGain;
        this.militaryGain = 0;
        this.oneOfResources = new ArrayList<>();
    }

    Wonder(Cost cost, boolean immediatelyPlaySecongdTurn, Integer victoryPoints, Integer monetaryGain, Integer militaryGain) {
        this.cost = cost;
        this.immediatelyPlaySecondTurn = immediatelyPlaySecongdTurn;
        this.victoryPoints = victoryPoints;
        this.monetaryGain = monetaryGain;
        this.militaryGain = militaryGain;
        this.oneOfResources = new ArrayList<>();
    }

    Wonder(Cost cost, boolean immediatelyPlaySecondTurn, Integer victoryPoints, Integer monetaryGain, List<Resource> oneOfResources) {
        this.cost = cost;
        this.immediatelyPlaySecondTurn = immediatelyPlaySecondTurn;
        this.victoryPoints = victoryPoints;
        this.monetaryGain = monetaryGain;
        this.militaryGain = 0;
        this.oneOfResources = oneOfResources;
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
}
