package com.shannontheoret.duel;

import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.dao.GameDao;
import com.shannontheoret.duel.dao.MilitaryDao;
import com.shannontheoret.duel.dao.PlayerDao;
import com.shannontheoret.duel.entity.Game;
import com.shannontheoret.duel.entity.Military;
import com.shannontheoret.duel.entity.Player;
import com.shannontheoret.duel.exceptions.InvalidMoveException;
import com.shannontheoret.duel.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    GameDao gameDao;

    @Mock
    PlayerDao playerDao;

    @Mock
    MilitaryDao militaryDao;

    @InjectMocks
    private GameService gameService;

    @Test
    public void newGameTest() throws InvalidMoveException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        assertNotNull(game, "Game should not be null");
        assertNotNull(game.getCode(), "Code should not be null");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Step should be PLAY_CARD");
        assertEquals(1, game.getAge(), "Age should be 1");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1");
        assertNewPlayer(game.getPlayer1());
        assertNewPlayer(game.getPlayer2());
        assertEquals(game.getPlayer1(), game.findActivePlayer(), "Active player should be player 1");
        assertEquals(game.getPlayer2(), game.findNonActivePlayer(), "Nonactive player should be player 2");
        assertNotNull(game.getMilitary(), "Military should not be null");
        assertEquals(0, game.getMilitary().getMilitaryPosition(), "Military position should be 0");
        assertTrue(game.getMilitary().getLoot2Player1Available(), "Loot 2 player 1 should be available.");
        assertTrue(game.getMilitary().getLoot2Player2Available(), "Loot 2 player 2 should be available.");
        assertTrue(game.getMilitary().getLoot5Player1Available(), "Loot 5 player 1 should be available.");
        assertTrue(game.getMilitary().getLoot5Player2Available(), "Loot 5 player 2 should be available.");
        assertNotNull(game.getPyramid(), "Pyramid should not be null.");
        assertEquals(20, game.getPyramid().size(), "Pyramid should be 20 in size.");
        assertUniqueCards(game.getPyramid());
        assertAllCardsInAge(game.getPyramid(), 1);
        assertPyramidContainsIndexesUpTo(game.getPyramid(), 19);
        assertNotNull(game.getDiscardedCards(), "Discarded cards should not be null.");
        assertTrue(game.getDiscardedCards().isEmpty(), "Discarded cards should be empty.");
        assertNotNull(game.getTokensAvailable(), "Tokens available should not be null.");
        assertEquals(5, game.getTokensAvailable().size(), "There should be 5 tokens available.");
        assertNotNull(game.getTokensUnavailable(), "Tokens unavailable should not be null");
        assertEquals(5, game.getTokensUnavailable().size(), "There should be 5 tokens unavailable.");
        assertNoSubset(game.getTokensAvailable(), game.getTokensUnavailable());
    }

    private void assertNewPlayer(Player player) {
        assertNotNull(player.getHand(), "Player hand should not be null");
        assertTrue(player.getHand().isEmpty(), "Player hand should be empty");
        assertEquals(7, player.getMoney(), "Player should have 7 coins");
        assertNotNull(player.getTokens(), "Player tokens should not be null");
        assertTrue(player.getTokens().isEmpty(), "Player tokens should be empty");
        assertFalse(player.getWon(), "Player should not have won");
        assertNotNull(player.getSortedHand(), "Player sorted hand should not be null");
        assertTrue(player.getSortedHand().isEmpty(), "Plauer sorted hand should be empty");
        assertFalse(player.checkScienceVictory());
    }

    private void assertUniqueCards(Map<Integer, CardName> pyramid) {
        Set<CardName> uniqueValues = new HashSet<>();

        for (CardName cardName : pyramid.values()) {
            assertTrue(uniqueValues.add(cardName), "Duplicate card found in pyramid: " + cardName.name());
        }
    }

    private void assertAllCardsInAge(Map<Integer, CardName> pyramid, Integer age) {
        for (CardName cardName : pyramid.values()) {
            assertEquals(age, cardName.getCard().getAge(), "Card " + cardName.name() + " not in age " + age + ".");
        }
    }

    private void assertPyramidContainsIndexesUpTo(Map<Integer, CardName> pyramid, Integer indexMax) {
        Set<Integer> expectedIndexes = new HashSet<>();
        for (int i = 0; i <= indexMax; i++) {
            expectedIndexes.add(i);
        }
        assertEquals(expectedIndexes, pyramid.keySet(), "Map does not contain all expected keys.");
    }

    private void assertNoSubset(Set<?> set1, Set<?> set2) {
        Set<?> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        assertTrue(intersection.isEmpty(), "Sets share a common subset: " + intersection);
    }

}
