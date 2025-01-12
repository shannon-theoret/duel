package com.shannontheoret.duel;

import com.shannontheoret.duel.card.Card;
import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.dao.GameDao;
import com.shannontheoret.duel.dao.MilitaryDao;
import com.shannontheoret.duel.dao.PlayerDao;
import com.shannontheoret.duel.entity.Game;
import com.shannontheoret.duel.entity.Military;
import com.shannontheoret.duel.entity.Player;
import com.shannontheoret.duel.exceptions.GameCodeNotFoundException;
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
import static org.mockito.Mockito.*;

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

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void chooseProgressToken_valid() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getTokensAvailable().clear();
        game.getTokensAvailable().addAll(Set.of(ProgressToken.LAW, ProgressToken.STRATEGY));
        game.getTokensUnavailable().clear();
        game.getTokensUnavailable().addAll(Set.of(ProgressToken.ARCHITECTURE, ProgressToken.AGRICULTURE, ProgressToken.MASONRY, ProgressToken.PHILOSOPHY, ProgressToken.THEOLOGY));

        game.getPlayer1().getTokens().addAll(Set.of(ProgressToken.ECONOMY));
        game.getPlayer2().getTokens().addAll(Set.of(ProgressToken.MATHEMATICS, ProgressToken.URBANISM));
        game.getPlayer1().setMoney(3);
        game.getPlayer2().setMoney(4);
        game.setCurrentPlayerNumber(1);

        game.setStep(GameStep.CHOOSE_SCIENCE);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.chooseProgressToken("123", ProgressToken.LAW);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertFalse(game.getTokensAvailable().contains(ProgressToken.LAW), "Tokens available should not contain LAW.");
        assertFalse(game.getTokensUnavailable().contains(ProgressToken.LAW), "Tokens unavailable should not contain LAW.");
        assertTrue(game.getPlayer1().getTokens().contains(ProgressToken.LAW), "Player should have LAW progress token.");
        assertEquals(2, game.getPlayer2().getTokens().size(), "Player should have two tokens.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Should be player 2 turn.");
        assertEquals(3, game.getPlayer1().getMoney(), "Player 1 money should remain at 3.");
        assertEquals(4, game.getPlayer2().getMoney(), "Player 2 money should remain at 4.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());

    }

    @Test
    public void chooseProgressToken_incorrectStep_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getTokensAvailable().clear();
        game.getTokensAvailable().addAll(Set.of(ProgressToken.MATHEMATICS, ProgressToken.STRATEGY));
        game.getTokensUnavailable().clear();
        game.getTokensUnavailable().addAll(Set.of(ProgressToken.ARCHITECTURE, ProgressToken.AGRICULTURE, ProgressToken.MASONRY, ProgressToken.PHILOSOPHY, ProgressToken.THEOLOGY));

        game.getPlayer1().getTokens().addAll(Set.of(ProgressToken.ECONOMY));
        game.getPlayer2().getTokens().addAll(Set.of(ProgressToken.LAW, ProgressToken.URBANISM));
        game.setCurrentPlayerNumber(1);

        game.setStep(GameStep.PLAY_CARD);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.chooseProgressToken("123", ProgressToken.STRATEGY), "Should throw InvalidMoveException for incorrect game step.");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());

    }

    @Test
    public void chooseProgressToken_unavailableToken_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getTokensAvailable().clear();
        game.getTokensAvailable().addAll(Set.of(ProgressToken.MATHEMATICS, ProgressToken.STRATEGY, ProgressToken.MASONRY));
        game.getTokensUnavailable().clear();
        game.getTokensUnavailable().addAll(Set.of(ProgressToken.ARCHITECTURE, ProgressToken.AGRICULTURE, ProgressToken.LAW, ProgressToken.PHILOSOPHY, ProgressToken.THEOLOGY));

        game.getPlayer1().getTokens().addAll(Set.of(ProgressToken.ECONOMY));
        game.getPlayer2().getTokens().addAll(Set.of(ProgressToken.URBANISM));

        game.setStep(GameStep.CHOOSE_SCIENCE);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.chooseProgressToken("123", ProgressToken.URBANISM), "Should throw InvalidMoveException for token unavailable.");
        assertThrows(InvalidMoveException.class, () -> gameService.chooseProgressToken("123", ProgressToken.ARCHITECTURE), "Should throw InvalidMoveException for token unavailable.");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void chooseProgressToken_gameEnd() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getTokensAvailable().clear();
        game.getTokensAvailable().addAll(Set.of(ProgressToken.MATHEMATICS, ProgressToken.STRATEGY, ProgressToken.MASONRY, ProgressToken.LAW));
        game.getTokensUnavailable().clear();
        game.getTokensUnavailable().addAll(Set.of(ProgressToken.ARCHITECTURE, ProgressToken.AGRICULTURE, ProgressToken.URBANISM, ProgressToken.PHILOSOPHY, ProgressToken.THEOLOGY));

        game.getPlayer1().setTokens(Set.of(ProgressToken.ECONOMY));

        game.setCurrentPlayerNumber(2);

        game.getPlayer2().getHand().addAll(Set.of(CardName.DISPENSARY, CardName.LIBRARY, CardName.OBSERVATORY, CardName.STUDY, CardName.APOTHECARY, CardName.ALTER, CardName.LUMBER_YARD));


        game.setStep(GameStep.CHOOSE_SCIENCE);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.chooseProgressToken("123", ProgressToken.LAW);

        assertEquals(GameStep.GAME_END, game.getStep(), "LAW token purchase should trigger game end.");
        assertTrue(game.getPlayer2().getWon(), "Player 2 should have won.");
        assertFalse(game.getPlayer1().getWon(), "Player 1 should not have won.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void chooseProgressToken_agriculture_increaseMoneySix() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getTokensAvailable().clear();
        game.getTokensAvailable().addAll(Set.of(ProgressToken.AGRICULTURE, ProgressToken.MATHEMATICS, ProgressToken.STRATEGY, ProgressToken.MASONRY, ProgressToken.LAW));
        game.getTokensUnavailable().clear();
        game.getTokensUnavailable().addAll(Set.of(ProgressToken.ARCHITECTURE, ProgressToken.ECONOMY, ProgressToken.URBANISM, ProgressToken.PHILOSOPHY, ProgressToken.THEOLOGY));

        game.setStep(GameStep.CHOOSE_SCIENCE);

        game.getPlayer1().setMoney(8);
        game.getPlayer2().setMoney(2);

        game.setCurrentPlayerNumber(1);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.chooseProgressToken("123", ProgressToken.AGRICULTURE);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertFalse(game.getTokensAvailable().contains(ProgressToken.AGRICULTURE), "Tokens available should not contain AGRICULTURE.");
        assertFalse(game.getTokensUnavailable().contains(ProgressToken.AGRICULTURE), "Tokens unavailable should not contain AGRICULTURE.");
        assertTrue(game.getPlayer1().getTokens().contains(ProgressToken.AGRICULTURE), "Player should have AGRICULTURE progress token.");
        assertEquals(1, game.getPlayer1().getTokens().size(), "Player should have one token.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Should be player 2 turn.");
        assertEquals(14, game.getPlayer1().getMoney(), "Player 1 money should have increased from 8 to 14.");
        assertEquals(2, game.getPlayer2().getMoney(), "Player 2 money should remain at 2.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void chooseProgressToken_urbanism_increaseMoneySix() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getTokensAvailable().clear();
        game.getTokensAvailable().addAll(Set.of(ProgressToken.AGRICULTURE, ProgressToken.MATHEMATICS, ProgressToken.STRATEGY, ProgressToken.MASONRY, ProgressToken.URBANISM));
        game.getTokensUnavailable().clear();
        game.getTokensUnavailable().addAll(Set.of(ProgressToken.ARCHITECTURE, ProgressToken.ECONOMY, ProgressToken.LAW, ProgressToken.PHILOSOPHY, ProgressToken.THEOLOGY));

        game.setStep(GameStep.CHOOSE_SCIENCE);

        game.getPlayer1().setMoney(4);
        game.getPlayer2().setMoney(0);

        game.setCurrentPlayerNumber(2);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.chooseProgressToken("123", ProgressToken.URBANISM);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertFalse(game.getTokensAvailable().contains(ProgressToken.URBANISM), "Tokens available should not contain LAW.");
        assertFalse(game.getTokensUnavailable().contains(ProgressToken.URBANISM), "Tokens unavailable should not contain LAW.");
        assertTrue(game.getPlayer2().getTokens().contains(ProgressToken.URBANISM), "Player should have LAW progress token.");
        assertEquals(1, game.getPlayer2().getTokens().size(), "Player 2 should have one token.");
        assertEquals(1, game.getCurrentPlayerNumber(), "Should be player 1 turn.");
        assertEquals(4, game.getPlayer1().getMoney(), "Player 1 money should remain at 4.");
        assertEquals(6, game.getPlayer2().getMoney(), "Player 2 money should have increased from 0 to 6.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
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
