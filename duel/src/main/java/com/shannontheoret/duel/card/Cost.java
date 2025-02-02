package com.shannontheoret.duel.card;

import com.shannontheoret.duel.Wonder;

import java.util.*;
import java.util.stream.Collectors;

public class Cost {
    private final Integer monetaryCost;
    private final List<Resource> resourceCost;
    private final CardName freeBuildWith;

    public Cost() {
        this.monetaryCost = 0;
        this.resourceCost = new ArrayList<>();
        this.freeBuildWith = null;
    }

    public Cost(Integer monetaryCost) {
        this.monetaryCost = monetaryCost;
        this.resourceCost = new ArrayList<>();
        this.freeBuildWith = null;
    }

    public Cost(List<Resource> resourceCost) {
        this.monetaryCost = 0;
        this.resourceCost = resourceCost;
        this.freeBuildWith = null;
    }

    public Cost(Integer monetaryCost, List<Resource> resourceCost) {
        this.monetaryCost = monetaryCost;
        this.resourceCost = resourceCost;
        this.freeBuildWith = null;
    }

    public Cost(Integer monetaryCost, List<Resource> resourceCost, CardName freeBuildWith) {
        this.monetaryCost = monetaryCost;
        this.resourceCost = resourceCost;
        this.freeBuildWith = freeBuildWith;
    }

    public Integer getMonetaryCost() {
        return monetaryCost;
    }

    public List<Resource> getResourceCost() {
        return resourceCost;
    }

    public CardName getFreeBuildWith() {
        return freeBuildWith;
    }

    public Integer calculateTotalMonetaryCost(Set<CardName> playerHand, Set<CardName> opponentHand) {
        return calculateTotalMonetaryCost(playerHand, opponentHand, EnumSet.noneOf(Wonder.class), false);
    }
    public Integer  calculateTotalMonetaryCost(Set<CardName> playerHand, Set<CardName> opponentHand, Set<Wonder> wonders, Boolean buildWithTwoFewer) {
        if (constructForFree(playerHand)) {
            return 0;
        }
        Integer totalCost = monetaryCost;
        if (resourceCost.isEmpty()) {
            return totalCost;
        }
        Set<ResourceCard> resourceCardsInHand = playerHand.stream()
                .filter(card -> card.getCard().getCardType() == CardOrValueType.RAW_MATERIAL || card.getCard().getCardType() == CardOrValueType.MANUFACTURED_GOOD)
                .map(card -> (ResourceCard) card.getCard())
                .collect(Collectors.toSet());
        List<Resource> allResourcesAvailable = new ArrayList<>();
        for (ResourceCard cardInHand : resourceCardsInHand) {
            allResourcesAvailable.addAll(cardInHand.getResourcesProduced());
        }
        List<Resource> resourcesLeftToPay = new ArrayList<>();
        for (Resource resource: resourceCost) {
            if (allResourcesAvailable.contains(resource)) {
                allResourcesAvailable.remove(resource);
            } else {
                resourcesLeftToPay.add(resource);
            }
        }
        if (resourcesLeftToPay.isEmpty()) {
            return totalCost;
        }
        Map<Resource, Integer> costToTrade = new HashMap<>();
        Set<CommercialBuildingCard> commercialBuildingCardsInHand = playerHand.stream()
                .filter(card -> card.getCard().getCardType() == CardOrValueType.COMMERCIAL_BUILDING)
                .map(card -> (CommercialBuildingCard) card.getCard())
                .collect(Collectors.toSet());
        for (CommercialBuildingCard yellow : commercialBuildingCardsInHand) {
            for (Resource resourceForOne: yellow.getTradeForOne()) {
                costToTrade.put(resourceForOne, 1);
            }
        }
        Set<ResourceCard> resourceCardsInOpponentsHand = opponentHand.stream()
                .filter(card -> card.getCard().getCardType() == CardOrValueType.RAW_MATERIAL || card.getCard().getCardType() == CardOrValueType.MANUFACTURED_GOOD)
                .map(card -> (ResourceCard) card.getCard())
                .collect(Collectors.toSet());
        Map<Resource, Integer> allResourcesAvailableToOpponent = new HashMap<>();
        for (ResourceCard cardInHand : resourceCardsInOpponentsHand) {
            for(Resource resource : cardInHand.getResourcesProduced()) {
                if (allResourcesAvailableToOpponent.containsKey(resource)) {
                    allResourcesAvailableToOpponent.put(resource, allResourcesAvailableToOpponent.get(resource) + 1);
                } else {
                    allResourcesAvailableToOpponent.put(resource, 1);
                }
            }
        }
        for (Resource resource : Resource.values()) {
            if (!costToTrade.containsKey(resource)) {
                if (allResourcesAvailableToOpponent.containsKey(resource)) {
                    costToTrade.put(resource, 2 + allResourcesAvailableToOpponent.get(resource));
                } else {
                    costToTrade.put(resource, 2);
                }
            }
        }
        for (Wonder wonder : wonders) {
            if (!wonder.getOneOfResources().isEmpty()) {
                Set<Resource> resourcesLeftToPayAvailableFree = new HashSet<>(wonder.getOneOfResources());
                resourcesLeftToPayAvailableFree.retainAll(resourcesLeftToPay);
                if(!resourcesLeftToPayAvailableFree.isEmpty()) {
                    Resource mostExpensiveResource = null;
                    Integer mostExpensiveCost = 0;
                    for(Resource resource : resourcesLeftToPayAvailableFree) {
                        Integer cost = costToTrade.get(resource);
                        if (cost > mostExpensiveCost) {
                            mostExpensiveResource = resource;
                            mostExpensiveCost = cost;
                        }
                    }
                    resourcesLeftToPay.remove(mostExpensiveResource);
                }
            }
        }
        for (CommercialBuildingCard yellow : commercialBuildingCardsInHand) {
            Set<Resource> resourcesLeftToPayAvailableFree = new HashSet<>(yellow.getOneOfResources());
            resourcesLeftToPayAvailableFree.retainAll(resourcesLeftToPay);
            if(!resourcesLeftToPayAvailableFree.isEmpty()) {


                Resource mostExpensiveResource = null;
                Integer mostExpensiveCost = 0;
                for(Resource resource : resourcesLeftToPayAvailableFree) {
                    Integer cost = costToTrade.get(resource);
                    if (cost > mostExpensiveCost) {
                        mostExpensiveResource = resource;
                        mostExpensiveCost = cost;
                    }
                }
                resourcesLeftToPay.remove(mostExpensiveResource);
            }
        }
        if (buildWithTwoFewer) {
            if (resourcesLeftToPay.size() > 2) {
                Resource mostExpensiveResource = null;
                Integer mostExpensiveCost = 0;
                Resource secondMostExpensiveResource = null;
                Integer secondMostExpensiveCost = 0;
                for(Resource resource : resourcesLeftToPay) {
                    Integer cost = costToTrade.get(resource);
                    if (cost > mostExpensiveCost) {
                        secondMostExpensiveResource = mostExpensiveResource;
                        secondMostExpensiveCost = mostExpensiveCost;
                        mostExpensiveResource = resource;
                        mostExpensiveCost = cost;
                    } else if (cost > secondMostExpensiveCost) {
                        secondMostExpensiveCost = cost;
                        secondMostExpensiveResource = resource;
                    }
                }
                resourcesLeftToPay.remove(mostExpensiveResource);
                resourcesLeftToPay.remove(secondMostExpensiveResource);
            } else {
                return totalCost;
            }
        }
        for (Resource resource : resourcesLeftToPay) {
            totalCost += costToTrade.get(resource);
        }
        return totalCost;
    }

    public Boolean constructForFree(Set<CardName> playerHand) {
        return (freeBuildWith != null && playerHand.contains(freeBuildWith));
    }

}
