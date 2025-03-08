package com.shannontheoret.duel.utility;

import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.Wonder;
import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.card.CardOrValueType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreUtilityTest {

    @Test
    public void calculateSimpleCategoryScore() {
        assertEquals(20, ScoreUtility.calculateSimpleCategoryScore(
                Set.of(CardName.THEATRE, CardName.ARENA, CardName.GLASSWORKS, CardName.STABLE, CardName.LIGHTHOUSE,
                        CardName.MERCHANTS_GUILD, CardName.SENATE, CardName.BATHS, CardName.PHARMACIST, CardName.TEMPLE,
                        CardName.ARSENAL, CardName.STONE_RESERVE, CardName.FORTIFICATIONS, CardName.UNIVERSITY, CardName.STUDY,
                        CardName.SCRIPTORIUM, CardName.ARCHERY_RANGE, CardName.AQUEDUCT, CardName.BUILDERS_GUILD, CardName.QUARRY),
                CardOrValueType.CIVILIAN_BUILDING));
    }

    @Test
    public void calculateSimpleCategoryScore_noCardsInCategory() {
        assertEquals(0,
                ScoreUtility.calculateSimpleCategoryScore(Set.of(CardName.FORUM, CardName.TAVERN, CardName.GARDENS, CardName.PANTHEON, CardName.THEATRE),
                CardOrValueType.SCIENTIFIC_BUILDING));
    }

    @Test
    public void calculateWonderScore() {
        assertEquals(12,ScoreUtility.calculateWonderScore(Set.of(Wonder.CIRCUS_MAXIMUS, Wonder.THE_COLOSSUS, Wonder.THE_SPHINX)));
    }

    @Test
    public void calculateProgressTokenScore() {
        assertEquals(13, ScoreUtility.calculateProgressTokenScore(Set.of(ProgressToken.LAW, ProgressToken.MATHEMATICS, ProgressToken.AGRICULTURE)));
    }

    @Test
    public void calculateMoneyScore() {
        assertEquals(2, ScoreUtility.calculateMoneyScore(7));
    }

    @Test
    public void calculatePlayer1MilitaryScore() {
        assertEquals(0, ScoreUtility.calculatePlayer1MilitaryScore(-2));
        assertEquals(2, ScoreUtility.calculatePlayer1MilitaryScore(1));
        assertEquals(5, ScoreUtility.calculatePlayer1MilitaryScore(3));
        assertEquals(10, ScoreUtility.calculatePlayer1MilitaryScore(8));
    }

    @Test
    public void calculatePlayer2MilitaryScore() {
        assertEquals(0, ScoreUtility.calculatePlayer2MilitaryScore(6));
        assertEquals(2, ScoreUtility.calculatePlayer2MilitaryScore(-2));
        assertEquals(5, ScoreUtility.calculatePlayer2MilitaryScore(-5));
        assertEquals(10, ScoreUtility.calculatePlayer2MilitaryScore(-7));
    }

    @Test
    public void calculateTotal() {
        Map<CardOrValueType, Integer> scores = Map.of(
                CardOrValueType.MONEY, 2,
                CardOrValueType.WONDER, 9,
                CardOrValueType.PROGRESS_TOKEN, 0,
                CardOrValueType.MILITARY_BUILDING, 5,
                CardOrValueType.GUILD, 4,
                CardOrValueType.COMMERCIAL_BUILDING, 2,
                CardOrValueType.CIVILIAN_BUILDING, 8,
                CardOrValueType.SCIENTIFIC_BUILDING, 4);
        assertEquals(34, ScoreUtility.calculateTotal(scores));
    }
}
