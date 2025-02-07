package com.shannontheoret.duel.utility;

import com.shannontheoret.duel.GameStep;
import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.Wonder;
import com.shannontheoret.duel.card.*;
import com.shannontheoret.duel.entity.Military;
import com.shannontheoret.duel.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ScoreUtility {
    public static Integer calculateSimpleCategoryScore(Set<CardName> hand, CardOrValueType category) {
        Integer score = 0;
        Set<Card> cards = hand.stream()
                .filter(cardName -> cardName.getCard().getCardType() == category)
                .map(cardName -> cardName.getCard()).collect(Collectors.toSet());
        for (Card card : cards) {
            score += card.getVictoryPoints();
        }
        return score;
    }

    public static Integer calculateGuildScore(Player currentPlayer, Player opponent) {
        Integer score = 0;
        Set<GuildCard> guildCards = currentPlayer.getHand().stream()
                .filter(cardName -> cardName.getCard().getCardType() == CardOrValueType.GUILD)
                .map(cardName -> (GuildCard) cardName.getCard()).collect(Collectors.toSet());
        for (GuildCard guildCard : guildCards) {
            CardOrValueType type = guildCard.getCardOrValueTypeForVictoryPoints();
            Integer numOfType;
            switch (type) {
                case WONDER:
                    numOfType = Math.max(currentPlayer.calculateWondersConstructed().size(),opponent.calculateWondersConstructed().size());
                    break;
                case MONEY:
                    numOfType = Math.max(currentPlayer.getMoney(), opponent.getMoney());
                    break;
                case RAW_MATERIAL_AND_MANUFACTURED_GOOD:
                    Integer opponentCount = HandUtility.countNumberOfCardType(CardOrValueType.RAW_MATERIAL, opponent.getHand())
                            + HandUtility.countNumberOfCardType(CardOrValueType.MANUFACTURED_GOOD, opponent.getHand());
                    Integer playerCount = HandUtility.countNumberOfCardType(CardOrValueType.RAW_MATERIAL, currentPlayer.getHand())
                            + HandUtility.countNumberOfCardType(CardOrValueType.MANUFACTURED_GOOD, currentPlayer.getHand());
                    numOfType = Math.max(opponentCount, playerCount);
                    break;
                default:
                    numOfType = Math.max(HandUtility.countNumberOfCardType(type, currentPlayer.getHand()), HandUtility.countNumberOfCardType(type, opponent.getHand()));
                    break;
            }
            score += guildCard.getVictoryPoints(numOfType);
        }
        return score;
    }

    public static Integer calculateWonderScore(Set<Wonder> wonders) {
        Integer score = 0;
        for (Wonder wonder: wonders) {
            score += wonder.getVictoryPoints();
        }
        return score;
    }

    public static Integer calculateProgressTokenScore(Set<ProgressToken> tokens) {
        Integer score = 0;
        for (ProgressToken token: tokens) {
            switch (token) {
                case AGRICULTURE:
                    score += 4;
                    break;
                case PHILOSOPHY:
                    score += 7;
                    break;
                case MATHEMATICS:
                    score += 3 * tokens.size();
                    break;
                default:
                    break;
            }
        }
        return score;
    }

    public static Integer calculateMoneyScore(Integer money) {
        return money/3;
    }

    public static Integer calculatePlayer1MilitaryScore(Integer militaryPosition) {
        Integer score;
        if (militaryPosition <= 0) {
            score = 0;
        } else if (militaryPosition >= 1 && militaryPosition <= 2) {
            score = 2;
        } else if (militaryPosition >= 3 && militaryPosition <= 5) {
            score = 5;
        } else {
            score = 10;
        }
        return score;
    }

    public static Integer calculatePlayer2MilitaryScore(Integer militaryPosition) {
        Integer score;
        if (militaryPosition >= 0) {
            score = 0;
        } else if (militaryPosition >= -2 && militaryPosition <= -1) {
            score = 2;
        } else if (militaryPosition >= -5 && militaryPosition <= -3) {
            score = 5;
        } else {
            score = 10;
        }
        return score;
    }

    public static Integer calculateTotal(Map<CardOrValueType, Integer> score) {
        return score.values().stream().mapToInt(Integer::intValue).sum();
    }
}
