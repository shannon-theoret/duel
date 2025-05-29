package com.shannontheoret.duel;

import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.card.CardOrValueType;
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

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTests {

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
        assertEquals(GameStep.WONDER_SELECTION, game.getStep(), "Step should be WONDER_SELECTION");
        assertEquals(4, game.getWondersAvailable().size(), "Wonders available should be 4.");
        assertEquals(8, game.getWondersUnavailable().size(), "Wonders unavailable should be 8.");
        assertNoSubset(game.getWondersAvailable(), game.getTokensUnavailable());
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
    public void selectWonder_valid() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getWondersAvailable().clear();
        game.getWondersAvailable().addAll(Set.of(Wonder.THE_APPIAN_WAY, Wonder.CIRCUS_MAXIMUS, Wonder.THE_COLOSSUS, Wonder.THE_GREAT_LIBRARY));
        game.getWondersUnavailable().clear();
        game.getWondersUnavailable().addAll(Set.of(Wonder.THE_GREAT_LIGHTHOUSE, Wonder.THE_HANGING_GARDENS, Wonder.THE_MAUSOLEUM, Wonder.PIRAEUS, Wonder.THE_PYRAMIDS, Wonder.THE_SPHINX, Wonder.THE_STATUE_OF_ZEUS, Wonder.THE_TEMPLE_OF_ARTEMIS));
        game.setCurrentPlayerNumber(1);
        game.setStep(GameStep.WONDER_SELECTION);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.selectWonder("123", Wonder.CIRCUS_MAXIMUS);

        assertTrue(game.getPlayer1().getWonders().containsKey(Wonder.CIRCUS_MAXIMUS), "Player 1 has CIRCUS_MAXIMUS available for purchase.");
        assertFalse(game.getPlayer1().hasWonder(Wonder.CIRCUS_MAXIMUS), "Player 1 has not purchased CIRCUS_MAXIMUS.");
        assertEquals(Set.of(Wonder.THE_APPIAN_WAY, Wonder.THE_COLOSSUS, Wonder.THE_GREAT_LIBRARY), game.getWondersAvailable(), "Wonders available has removed CIRCUS_MAXIMUS.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");

        gameService.selectWonder("123", Wonder.THE_APPIAN_WAY);

        assertTrue(game.getPlayer2().getWonders().containsKey(Wonder.THE_APPIAN_WAY), "Player 2 should have THE_APPIAN_WAY available for purchase.");
        assertFalse(game.getPlayer2().hasWonder(Wonder.THE_APPIAN_WAY), "Player 2 should not have purchased THE_APPIAN_WAY.");
        assertEquals(Set.of(Wonder.THE_COLOSSUS, Wonder.THE_GREAT_LIBRARY), game.getWondersAvailable(), "Wonders available has removed CIRCUS_MAXIMUS.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");

        gameService.selectWonder("123", Wonder.THE_COLOSSUS);

        assertTrue(game.getPlayer2().getWonders().containsKey(Wonder.THE_COLOSSUS), "Player 2 should have THE_COLOSSUS available for purchase.");
        assertTrue(game.getPlayer1().getWonders().containsKey(Wonder.THE_GREAT_LIBRARY), "Player 1 should have THE_GREAT_LIBRARY available for purchase.");
        assertEquals(4, game.getWondersAvailable().size(), "Wonders available should have 4 wonders.");
        assertEquals(4, game.getWondersUnavailable().size(), "Wonders unavailable should have 4 wonders.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");

        game.getWondersAvailable().clear();
        game.getWondersAvailable().addAll(Set.of(Wonder.THE_GREAT_LIGHTHOUSE, Wonder.THE_HANGING_GARDENS, Wonder.THE_MAUSOLEUM, Wonder.PIRAEUS));
        game.getWondersUnavailable().clear();
        game.getWondersUnavailable().addAll(Set.of(Wonder.THE_PYRAMIDS, Wonder.THE_SPHINX, Wonder.THE_STATUE_OF_ZEUS, Wonder.THE_TEMPLE_OF_ARTEMIS));

        gameService.selectWonder("123", Wonder.THE_GREAT_LIGHTHOUSE);

        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertTrue(game.getPlayer2().getWonders().containsKey(Wonder.THE_GREAT_LIGHTHOUSE), "Player 2 should have THE_GREAT_LIGHTHOUSE available for purchase.");

        gameService.selectWonder("123", Wonder.THE_HANGING_GARDENS);

        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertTrue(game.getPlayer1().getWonders().containsKey(Wonder.THE_HANGING_GARDENS), "Player 1 should have THE_HANGING_GARDENS.");

        gameService.selectWonder("123", Wonder.THE_MAUSOLEUM);

        assertTrue(game.getPlayer1().getWonders().containsKey(Wonder.THE_MAUSOLEUM), "Player 1 contains wonder MAUSOLEUM.");
        assertTrue(game.getPlayer2().getWonders().containsKey(Wonder.PIRAEUS), "Player 2 contains wonder PIRAEUS.");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player should be 1.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game Step should be PLAY_CARD.");

        verify(gameDao, times(7)).save(game);
        verify(playerDao, times(14)).save(any(Player.class));
        verify(militaryDao, times(7)).save(game.getMilitary());
    }

    @Test
    public void selectWonder_wrongStepThrowsInvalidMoveException() {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getWondersAvailable().clear();
        game.getWondersAvailable().addAll(Set.of(Wonder.THE_PYRAMIDS, Wonder.THE_SPHINX, Wonder.THE_STATUE_OF_ZEUS));
        game.setStep(GameStep.PLAY_CARD);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.selectWonder("123", Wonder.THE_SPHINX), "Should throw InvalidMoveException for wrong step.");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void selectWonder_unavailableWonderThrowsInvalidMoveException() {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.getWondersAvailable().clear();
        game.getWondersAvailable().addAll(Set.of(Wonder.THE_PYRAMIDS, Wonder.THE_SPHINX, Wonder.THE_STATUE_OF_ZEUS));
        game.setStep(GameStep.WONDER_SELECTION);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.selectWonder("123", Wonder.PIRAEUS), "Should throw InvalidMoveException for wonder not available.");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_appianWay() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer1().getWonders().putAll(Map.of(
                Wonder.THE_APPIAN_WAY, 0,
                Wonder.CIRCUS_MAXIMUS, 0,
                Wonder.THE_SPHINX, 0,
                Wonder.THE_STATUE_OF_ZEUS, 0
                ));
        game.setAge(1);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().setMoney(10);
        game.getPlayer2().setMoney(6);
        game.getPlayer1().getHand().addAll(Set.of(CardName.CLAY_PIT, CardName.CLAY_POOL));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STONE_PIT));
        game.getMilitary().setMilitaryPosition(2);

        assertEquals(8, Wonder.THE_APPIAN_WAY.getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructWonder("123", 19, Wonder.THE_APPIAN_WAY);

        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertEquals(5, game.getPlayer1().getMoney(), "Player 1 money should be 5.");
        assertEquals(3, game.getPlayer2().getMoney(), "Player 2 money should be 3.");
        assertTrue(game.getPlayer1().hasWonder(Wonder.THE_APPIAN_WAY));
        assertEquals(Set.of(Wonder.THE_APPIAN_WAY), game.getPlayer1().calculateWondersConstructed());
        assertEquals(1, game.getPlayer1().getWonders().get(Wonder.THE_APPIAN_WAY), "Age constructed should be 1.");
        assertEquals(GameStep.PLAY_CARD, game.getStep());
        assertEquals(2, game.getMilitary().getMilitaryPosition());

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_colossus_architecture() throws GameCodeNotFoundException, InvalidMoveException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer1().getWonders().putAll(Map.of(
                Wonder.THE_COLOSSUS, 0,
                Wonder.CIRCUS_MAXIMUS, 0,
                Wonder.THE_SPHINX, 1,
                Wonder.THE_STATUE_OF_ZEUS, 0
        ));
        game.setAge(2);
        game.setPyramid(createAgeTwoPyramid());
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().setMoney(10);
        game.getPlayer2().setMoney(6);
        game.getPlayer1().getHand().addAll(Set.of(CardName.CLAY_PIT));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STONE_PIT, CardName.CLAY_POOL));
        game.getMilitary().setMilitaryPosition(2);
        game.getPlayer1().getTokens().add(ProgressToken.ARCHITECTURE);

        assertEquals(2, Wonder.THE_COLOSSUS.getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand(), Set.of(Wonder.THE_SPHINX), true), "Cost should be 2.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructWonder("123", 19, Wonder.THE_COLOSSUS);

        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertEquals(8, game.getPlayer1().getMoney(), "Player 1 money should be 8.");
        assertEquals(4, game.getPlayer2().getMoney(), "Player 2 money should be 4.");
        assertTrue(game.getPlayer1().hasWonder(Wonder.THE_COLOSSUS), "Player 1 should have THE_COLOSSUS.");
        assertEquals(Set.of(Wonder.THE_COLOSSUS, Wonder.THE_SPHINX), game.getPlayer1().calculateWondersConstructed(), "Player 1 should have added THE_COLOSSUS.");
        assertEquals(2, game.getPlayer1().getWonders().get(Wonder.THE_COLOSSUS), "Age constructed should be 2.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(4, game.getMilitary().getMilitaryPosition(), "Military position should be 4.");
        assertFalse(game.getMilitary().getLoot2Player2Available(), "Loot 2 player 2 should not be available.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_circusMaximus_economy() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer2().getWonders().putAll(Map.of(
                Wonder.THE_COLOSSUS, 0,
                Wonder.CIRCUS_MAXIMUS, 0,
                Wonder.THE_SPHINX, 1,
                Wonder.THE_GREAT_LIGHTHOUSE, 2
        ));
        game.setAge(2);
        game.setPyramid(createAgeTwoPyramid());
        game.setCurrentPlayerNumber(2);
        game.getPlayer1().setMoney(10);
        game.getPlayer2().setMoney(6);
        game.getPlayer1().getHand().addAll(Set.of(CardName.CLAY_PIT, CardName.LUMBER_YARD, CardName.PRESS));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STONE_PIT, CardName.CLAY_POOL));
        game.getMilitary().setMilitaryPosition(2);
        game.getPlayer1().getTokens().addAll(Set.of(ProgressToken.ARCHITECTURE, ProgressToken.ECONOMY));

        assertEquals(4, Wonder.CIRCUS_MAXIMUS.getCost().calculateTotalMonetaryCost(game.getPlayer2().getHand(), game.getPlayer1().getHand(), Set.of(Wonder.THE_SPHINX, Wonder.THE_GREAT_LIGHTHOUSE), false), "Cost should be 4.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructWonder("123", 18, Wonder.CIRCUS_MAXIMUS);

        assertFalse(game.getPyramid().containsKey(18), "Pyramid should not contain index 18");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertEquals(14, game.getPlayer1().getMoney(), "Player 1 money should be 14.");
        assertEquals(2, game.getPlayer2().getMoney(), "Player 2 money should be 2.");
        assertTrue(game.getPlayer2().hasWonder(Wonder.CIRCUS_MAXIMUS), "Player 2 should have CIRCUS_MAXIMUS.");
        assertEquals(Set.of(Wonder.THE_GREAT_LIGHTHOUSE, Wonder.THE_SPHINX, Wonder.CIRCUS_MAXIMUS), game.getPlayer2().calculateWondersConstructed(), "Player 2 should have added CIRCUS_MAXIMUS.");
        assertEquals(2, game.getPlayer2().getWonders().get(Wonder.CIRCUS_MAXIMUS), "Age constructed should be 2.");
        assertEquals(GameStep.DESTROY_GREY, game.getStep(), "Game step should be DESTROY_GREY.");
        assertEquals(1, game.getMilitary().getMilitaryPosition(), "Military position should be 1.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_greatLibrary() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer1().getWonders().putAll(Map.of(
                Wonder.THE_GREAT_LIBRARY, 0,
                Wonder.CIRCUS_MAXIMUS, 0,
                Wonder.THE_SPHINX, 0,
                Wonder.THE_STATUE_OF_ZEUS, 0
        ));
        game.setAge(2);
        game.setPyramid(createAgeTwoPyramid());
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().setMoney(10);
        game.getPlayer2().setMoney(6);

        assertEquals(10, Wonder.THE_GREAT_LIBRARY.getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()), "Cost should be 10.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructWonder("123", 19, Wonder.THE_GREAT_LIBRARY);

        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertEquals(0, game.getPlayer1().getMoney(), "Player 1 money should be 0.");
        assertEquals(GameStep.CHOOSE_PROGRESS_TOKEN_FROM_DISCARD, game.getStep(), "Game step should be CHOOSE_PROGRESS_TOKEN_FROM_DISCARD.");
        assertEquals(3, game.getTokensFromUnavailable().size(), "Tokens from unavailable should have three tokens.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_theology() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer1().getWonders().putAll(Map.of(
                Wonder.THE_PYRAMIDS, 0,
                Wonder.CIRCUS_MAXIMUS, 0,
                Wonder.THE_SPHINX, 0,
                Wonder.THE_STATUE_OF_ZEUS, 0
        ));
        game.setAge(3);
        game.setPyramid(createAgeThreePyramid());
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().setMoney(17);
        game.getPlayer2().setMoney(6);
        game.getPlayer1().getTokens().add(ProgressToken.THEOLOGY);
        game.getPlayer2().getHand().addAll(Set.of(CardName.STONE_PIT, CardName.QUARRY));
        game.getPlayer1().getTokens().add(ProgressToken.THEOLOGY);

        assertEquals(14, Wonder.THE_PYRAMIDS.getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()), "Cost should be 14.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructWonder("123", 19, Wonder.THE_PYRAMIDS);

        assertFalse(game.getPyramid().containsKey(19), "Pyramid should not contain key 19.");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertEquals(3, game.getPlayer1().getMoney(), "Player 1 money should be 0.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_statueOfZeus() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().getWonders().putAll(Map.of(
                Wonder.THE_STATUE_OF_ZEUS, 0,
                Wonder.THE_GREAT_LIBRARY, 0,
                Wonder.PIRAEUS, 1,
                Wonder.THE_MAUSOLEUM, 1));
        game.getPlayer2().getHand().addAll(Set.of(CardName.QUARRY, CardName.PRESS, CardName.CLAY_RESERVE));
        game.getPlayer1().getHand().addAll(Set.of(CardName.LUMBER_YARD, CardName.STONE_PIT));
        game.getPlayer2().setMoney(10);
        game.getMilitary().setMilitaryPosition(1);
        game.getPlayer2().getTokens().add(ProgressToken.STRATEGY); //Has no impact on wonder military gain

        when(gameDao.findByCode("123")).thenReturn(game);

        assertEquals(4, Wonder.THE_STATUE_OF_ZEUS.getCost().calculateTotalMonetaryCost(game.getPlayer2().getHand(), game.getPlayer1().getHand(), game.getPlayer2().calculateWondersConstructed(), false));

        gameService.constructWonder("123", 19, Wonder.THE_STATUE_OF_ZEUS);

        assertEquals(GameStep.DESTROY_BROWN, game.getStep(), "Game step should be DESTROY_BROWN.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Get current player number.");
        assertEquals(0, game.getMilitary().getMilitaryPosition(), "Military position should be 0.");
        assertTrue(game.getPlayer2().hasWonder(Wonder.THE_STATUE_OF_ZEUS), "Player 2 should have THE_STATUE_OF_ZEUS.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_mausoleum() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().getWonders().putAll(Map.of(
                Wonder.THE_STATUE_OF_ZEUS, 0,
                Wonder.THE_GREAT_LIBRARY, 0,
                Wonder.PIRAEUS, 1,
                Wonder.THE_MAUSOLEUM, 0));
        game.getPlayer2().getHand().addAll(Set.of(CardName.QUARRY, CardName.PRESS, CardName.CLAY_RESERVE));
        game.getPlayer1().getHand().addAll(Set.of(CardName.LUMBER_YARD, CardName.STONE_PIT));
        game.getPlayer2().setMoney(10);
        game.getDiscardedCards().addAll(Set.of(CardName.LOGGING_CAMP, CardName.PALISADE));

        when(gameDao.findByCode("123")).thenReturn(game);

        assertEquals(4, Wonder.THE_MAUSOLEUM.getCost().calculateTotalMonetaryCost(game.getPlayer2().getHand(), game.getPlayer1().getHand(), game.getPlayer2().calculateWondersConstructed(), false));

        gameService.constructWonder("123", 19, Wonder.THE_MAUSOLEUM);

        assertEquals(GameStep.CONSTRUCT_FROM_DISCARD, game.getStep(), "Game step should be CONSTRUCT_FROM_DISCARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertTrue(game.getPlayer2().hasWonder(Wonder.THE_MAUSOLEUM), "Player 2 should have THE_MAUSOLEUM.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_wrongGameStep_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
            doNothing().when(gameDao).save(any(Game.class));
            doNothing().when(playerDao).save(any(Player.class));
            doNothing().when(militaryDao).save(any(Military.class));

            Game game = gameService.newGame();

            game.setStep(GameStep.WONDER_SELECTION);
            game.setCurrentPlayerNumber(2);
            game.getPlayer2().getWonders().putAll(Map.of(
                    Wonder.THE_STATUE_OF_ZEUS, 0,
                    Wonder.THE_GREAT_LIBRARY, 0,
                    Wonder.CIRCUS_MAXIMUS, 0,
                    Wonder.THE_MAUSOLEUM, 0));
            game.getPlayer2().setMoney(100);

            when(gameDao.findByCode("123")).thenReturn(game);

            assertThrows(InvalidMoveException.class, () -> gameService.constructWonder("123", 19, Wonder.THE_MAUSOLEUM));

            verify(gameDao, times(1)).save(game);
            verify(playerDao, times(2)).save(any(Player.class));
            verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_invalidIndex_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
            doNothing().when(gameDao).save(any(Game.class));
            doNothing().when(playerDao).save(any(Player.class));
            doNothing().when(militaryDao).save(any(Military.class));

            Game game = gameService.newGame();

            game.setStep(GameStep.PLAY_CARD);
            game.setCurrentPlayerNumber(2);
            game.getPlayer2().getWonders().putAll(Map.of(
                    Wonder.THE_STATUE_OF_ZEUS, 0,
                    Wonder.THE_GREAT_LIBRARY, 0,
                    Wonder.CIRCUS_MAXIMUS, 0,
                    Wonder.THE_MAUSOLEUM, 0));
            game.getPlayer2().setMoney(100);

            when(gameDao.findByCode("123")).thenReturn(game);

            assertThrows(InvalidMoveException.class, () -> gameService.constructWonder("123", 21, Wonder.THE_MAUSOLEUM));

            verify(gameDao, times(1)).save(game);
            verify(playerDao, times(2)).save(any(Player.class));
            verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_notEnoughMoney_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
            doNothing().when(gameDao).save(any(Game.class));
            doNothing().when(playerDao).save(any(Player.class));
            doNothing().when(militaryDao).save(any(Military.class));

            Game game = gameService.newGame();

            game.setStep(GameStep.PLAY_CARD);
            game.setCurrentPlayerNumber(1);
            game.getPlayer1().getWonders().putAll(Map.of(
                    Wonder.THE_STATUE_OF_ZEUS, 0,
                    Wonder.THE_GREAT_LIBRARY, 0,
                    Wonder.CIRCUS_MAXIMUS, 0,
                    Wonder.THE_MAUSOLEUM, 0));
            game.getPlayer1().setMoney(0);

            assertEquals(10, Wonder.THE_MAUSOLEUM.getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()));

            when(gameDao.findByCode("123")).thenReturn(game);

            assertThrows(InvalidMoveException.class, () -> gameService.constructWonder("123", 19, Wonder.THE_MAUSOLEUM));

            verify(gameDao, times(1)).save(game);
            verify(playerDao, times(2)).save(any(Player.class));
            verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_wonderNotAvailable_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().getWonders().putAll(Map.of(
                Wonder.THE_STATUE_OF_ZEUS, 0,
                Wonder.THE_GREAT_LIBRARY, 0,
                Wonder.CIRCUS_MAXIMUS, 0,
                Wonder.THE_MAUSOLEUM, 1));
        game.getPlayer2().setMoney(100);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.constructWonder("123", 19, Wonder.THE_SPHINX));

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_wonderAlreadyConstructed_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
            doNothing().when(gameDao).save(any(Game.class));
            doNothing().when(playerDao).save(any(Player.class));
            doNothing().when(militaryDao).save(any(Military.class));

            Game game = gameService.newGame();

            game.setStep(GameStep.PLAY_CARD);
            game.setCurrentPlayerNumber(2);
            game.getPlayer2().getWonders().putAll(Map.of(
                    Wonder.THE_STATUE_OF_ZEUS, 0,
                    Wonder.THE_GREAT_LIBRARY, 0,
                    Wonder.CIRCUS_MAXIMUS, 0,
                    Wonder.THE_MAUSOLEUM, 1));
            game.getPlayer2().setMoney(100);

            when(gameDao.findByCode("123")).thenReturn(game);

            assertThrows(InvalidMoveException.class, () -> gameService.constructWonder("123", 19, Wonder.THE_MAUSOLEUM));

            verify(gameDao, times(1)).save(game);
            verify(playerDao, times(2)).save(any(Player.class));
            verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructWonder_7wondersConstructed_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeThreePyramid());
        game.setAge(3);
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().getWonders().putAll(Map.of(
                Wonder.THE_STATUE_OF_ZEUS, 0,
                Wonder.THE_GREAT_LIBRARY, 1,
                Wonder.CIRCUS_MAXIMUS, 2,
                Wonder.THE_MAUSOLEUM, 1));
        game.getPlayer1().getWonders().putAll(Map.of(
                Wonder.THE_PYRAMIDS, 1,
                Wonder.THE_APPIAN_WAY, 2,
                Wonder.PIRAEUS, 3,
                Wonder.THE_COLOSSUS, 1
        ));
        game.getPlayer2().setMoney(100);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.constructWonder("123", 19, Wonder.THE_STATUE_OF_ZEUS));

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_commercialBuilding() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeOnePyramid());
        game.getPyramid().put(4, CardName.PRESS);
        game.getPyramid().put(16, CardName.TAVERN);
        game.getPlayer1().setMoney(0);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 16);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(Set.of(CardName.TAVERN), game.getPlayer1().getHand(), "Player hand should have TAVERN.");
        assertEquals(4, game.getPlayer1().getMoney(), "Player money should have increased from 0 to 4.");
        assertEquals(1, game.getAge(),"Age should remain 1.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_commercialBuildingWithMoneyPerType() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeThreePyramid());
        game.getPyramid().remove(19);
        game.setAge(3);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().setMoney(5);
        game.getPlayer1().getHand().addAll(Set.of(CardName.STONE_PIT, CardName.BATHS, CardName.WALLS, CardName.GARRISON));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STABLE, CardName.ALTER));
        game.getDiscardedCards().addAll(Set.of(CardName.AQUEDUCT, CardName.ARCHERY_RANGE));

        when(gameDao.findByCode("123")).thenReturn(game);

        assertEquals(4, CardName.ARMORY.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()));

        gameService.constructBuilding("123", 17);

        assertEquals(3, game.getPlayer1().getMoney(), "Player money should be 3.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(Set.of(CardName.STONE_PIT, CardName.BATHS, CardName.WALLS, CardName.GARRISON, CardName.ARMORY), game.getPlayer1().getHand(), "Player hand should have added ARMORY.");
        assertFalse(game.getPyramid().containsKey(17), "Pyramid should not contain key 17.");
        assertEquals(Set.of(CardName.AQUEDUCT, CardName.ARCHERY_RANGE), game.getDiscardedCards(), "Discarded cards should remain the same.");
        assertEquals(3, game.getAge(),"Age should remain 3.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_commercialBuildingWithMultipleMoneyPerType() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeThreePyramid());
        game.getPyramid().put(4, CardName.PANTHEON);
        game.getPyramid().put(18, CardName.CHAMBER_OF_COMMERCE);
        game.setAge(3);
        game.setCurrentPlayerNumber(2);
        game.getPlayer1().setMoney(4);
        game.getPlayer2().setMoney(3);
        game.getPlayer2().getHand().addAll(Set.of(CardName.STATUE, CardName.PALISADE, CardName.GLASSWORKS, CardName.PRESS));

        when(gameDao.findByCode("123")).thenReturn(game);


        assertEquals(2, CardName.CHAMBER_OF_COMMERCE.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer2().getHand(), game.getPlayer1().getHand()));

        gameService.constructBuilding("123", 18);

        assertEquals(7, game.getPlayer2().getMoney(), "Player 2 money should have increased from 3 to 7.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(Set.of(CardName.STATUE, CardName.PALISADE, CardName.GLASSWORKS, CardName.PRESS, CardName.CHAMBER_OF_COMMERCE), game.getPlayer2().getHand());
        assertFalse(game.getPyramid().containsKey(18), "Pyramid should not contain index 18.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_militarySupremacy() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getMilitary().setMilitaryPosition(-8);
        game.getMilitary().setLoot5Player1Available(false);
        game.getMilitary().setLoot2Player1Available(false);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.putAll(Map.of(0, CardName.LIGHTHOUSE,
                1, CardName.GARDENS,
                2, CardName.SENATE,
                3, CardName.SIEGE_WORKSHOP,
                4, CardName.CHAMBER_OF_COMMERCE));
        game.setPyramid(pyramid);
        game.setAge(3);
        game.getPlayer2().getHand().addAll(Set.of(CardName.ARCHERY_RANGE));
        game.setCurrentPlayerNumber(2);
        game.getPlayer1().setMoney(2);
        game.getPlayer2().setMoney(6);

        assertEquals(0, CardName.SIEGE_WORKSHOP.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer2().getHand(), game.getPlayer1().getHand()), "Cost should be 0.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 3);

        assertFalse(game.getPyramid().containsKey(3), "Pyramid should not contain index 3.");
        assertEquals(GameStep.GAME_END, game.getStep(), "Game step should be GAME_END.");
        assertTrue(game.getPlayer2().getWon(), "Player 2 should have won.");
        assertEquals(-9, game.getMilitary().getMilitaryPosition(), "Military position should be -9.");
        assertFalse(game.getMilitary().getLoot5Player1Available(), "Loot 5 player 1 should not be available.");
        assertEquals(Set.of(CardName.ARCHERY_RANGE, CardName.SIEGE_WORKSHOP), game.getPlayer2().getHand(), "Hand should have SIEGE_WORKSHOP added to it.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }


    @Test
    public void constructBuilding_militaryMultipleMilitaryPower() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getMilitary().setMilitaryPosition(2);
        game.getMilitary().setLoot2Player2Available(true);
        game.setPyramid(createAgeTwoPyramid());
        game.getPyramid().remove(19);
        game.getPyramid().remove(18);
        game.setAge(2);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.PALISADE, CardName.QUARRY, CardName.WOOD_RESERVE));
        game.getPlayer2().getHand().addAll(Set.of(CardName.TAVERN));
        game.getPlayer1().setMoney(8);
        game.getPlayer2().setMoney(3);
        game.getPlayer1().getTokens().add(ProgressToken.AGRICULTURE);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertEquals(3, CardName.ARCHERY_RANGE.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()), "Cost should equal 3.");

        gameService.constructBuilding("123", 16);

        assertEquals(Set.of(CardName.PALISADE, CardName.QUARRY, CardName.WOOD_RESERVE, CardName.ARCHERY_RANGE), game.getPlayer1().getHand(), "Player hand should have added ARCHERY_RANGE.");
        assertEquals(5, game.getPlayer1().getMoney(), "Player 1 money should have decreased from 8 to 5.");
        assertEquals(1, game.getPlayer2().getMoney(), "Player money should have decreased from 3 to 1.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertFalse(game.getPyramid().containsKey(16));
        assertEquals(4, game.getMilitary().getMilitaryPosition(), "Military position should have increased from 2 to 4.");
        assertFalse(game.getMilitary().getLoot2Player2Available(), "Loot 2 Player 1 should not be available.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_militaryWithStrategyToken_urbanismNotFreeBuild() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeOnePyramid());
        game.getPyramid().remove(19);
        game.getPyramid().remove(18);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getTokens().addAll(Set.of(ProgressToken.STRATEGY, ProgressToken.URBANISM));
        game.getPlayer1().setMoney(7);
        game.getPlayer2().setMoney(7);
        game.getMilitary().setMilitaryPosition(1);
        game.getMilitary().setLoot2Player2Available(false);

        assertEquals(2, CardName.GARRISON.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()), "Cost should be 2.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 13);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Currect player number should be 2.");
        assertEquals(1, game.getAge(), "Age should be 1.");
        assertEquals(Set.of(CardName.GARRISON), game.getPlayer1().getHand(), "Player 1 hand must have GARRISON.");
        assertFalse(game.getPyramid().containsKey(13));
        assertEquals(3, game.getMilitary().getMilitaryPosition(), "Military position is 3.");
        assertFalse(game.getMilitary().getLoot2Player2Available(), "Loot 2 player 2 should not be available.");
        assertEquals(5, game.getPlayer1().getMoney(), "PLayer 1 money should be 5.");
        assertEquals(7, game.getPlayer2().getMoney(), "PLayer 2 money should be 7.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_militaryMoneyOnlyDecreaseToZero() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeTwoPyramid());
        game.setCurrentPlayerNumber(2);
        game.setAge(2);
        game.getPlayer1().setMoney(1);
        game.getPlayer2().setMoney(6);
        game.getMilitary().setMilitaryPosition(-2);
        game.getMilitary().setLoot2Player1Available(true);

        assertEquals(3, CardName.BARRACKS.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer2().getHand(), game.getPlayer1().getHand()), "Cost should be 3.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 18);

        assertEquals(-3, game.getMilitary().getMilitaryPosition(), "Military position should be -3.");
        assertEquals(Set.of(CardName.BARRACKS), game.getPlayer2().getHand(), "Player 2 hand should contain BARRACKS.");
        assertFalse(game.getMilitary().getLoot2Player1Available(), "Loot 2 player 1 should not be available.");
        assertEquals(0, game.getPlayer1().getMoney(), "Player 1 money should be 0.");
        assertEquals(3, game.getPlayer2().getMoney(), "Player 2 money should be 3.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_scienceNoNewMatch_newAge() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.put(0, CardName.DISPENSARY);
        game.setPyramid(pyramid);
        game.setAge(2);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.WORKSHOP, CardName.LABORATORY, CardName.TAVERN, CardName.STONE_PIT, CardName.BRICKYARD));
        game.getPlayer1().setMoney(4);
        game.getPlayer1().getTokens().add(ProgressToken.URBANISM);

        assertEquals(0, CardName.DISPENSARY.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()), "Cost should be 0.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 0);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertEquals(4, game.getPlayer1().getMoney(), "Player 1 money should be 4.");
        assertEquals(Set.of(CardName.WORKSHOP, CardName.LABORATORY, CardName.TAVERN, CardName.STONE_PIT, CardName.BRICKYARD, CardName.DISPENSARY), game.getPlayer1().getHand());
        assertEquals(3, game.getAge(), "Age should be 3.");
        assertAllCardsInAge(game.getPyramid(), 3);
        assertEquals(Set.of(ProgressToken.URBANISM), game.getPlayer1().getTokens(), "Player 1 should have URBANISM token only.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_scienceMatch_masonryNonBlue() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.putAll(Map.of(0, CardName.SCHOOL));
        game.setPyramid(pyramid);
        game.setAge(2);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getTokens().add(ProgressToken.MASONRY);
        game.getPlayer1().getHand().addAll(Set.of(CardName.LIBRARY, CardName.APOTHECARY, CardName.BATHS, CardName.STABLE));
        game.getMilitary().setMilitaryPosition(7);

        assertEquals(6, CardName.SCHOOL.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()), "Monetary cost should be 6.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 0);

        assertEquals(GameStep.CHOOSE_PROGRESS_TOKEN, game.getStep(), "Game step should be CHOOSE_PROGRESS_TOKEN.");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1");
        assertEquals(2, game.getAge(), "Age should remain at 2");
        assertTrue(game.getPyramid().isEmpty(), "Pyramid should not have any cards.");
        assertEquals(7, game.getMilitary().getMilitaryPosition(), "Military position should remain at 7.");
        assertEquals(Set.of(CardName.LIBRARY, CardName.APOTHECARY, CardName.SCHOOL, CardName.BATHS, CardName.STABLE), game.getPlayer1().getHand(), "Hand should have added SCHOOL.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_scienceMatchNoAvailableTokens() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setAge(3);
        game.setPyramid(createAgeThreePyramid());
        game.getPyramid().remove(18);
        game.getTokensAvailable().clear();
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().getHand().addAll(Set.of(CardName.UNIVERSITY, CardName.ALTER));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 15);

        assertEquals(Set.of(CardName.UNIVERSITY, CardName.ALTER, CardName.OBSERVATORY), game.getPlayer2().getHand(), "Player 2 hand should include OBSERVATORY.");
        assertFalse(game.getPyramid().containsKey(15), "Pyramid should not contain index 15.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game stpe should be PLAY_CARD.");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertTrue(game.getTokensAvailable().isEmpty(), "There should be no tokens available.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_guildCurrentPlayerMoreCards() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setAge(3);
        game.setPyramid(createAgeThreePyramid());
        game.getPyramid().remove(19);
        game.getPyramid().remove(18);
        game.getPyramid().remove(17);
        game.getPyramid().remove(16);
        game.getPyramid().remove(15);
        game.setAge(3);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.ALTER, CardName.BATHS, CardName.ARCHERY_RANGE, CardName.BRICKYARD, CardName.STONE_PIT));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STATUE, CardName.DRYING_ROOM));

        assertEquals(7, CardName.MAGISTRATES_GUILD.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 13);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertEquals(Set.of(CardName.ALTER, CardName.BATHS, CardName.ARCHERY_RANGE, CardName.BRICKYARD, CardName.STONE_PIT, CardName.MAGISTRATES_GUILD), game.getPlayer1().getHand(), "Player 1 hand should have added MAGISTRATES_GUILD");
        assertFalse(game.getPyramid().containsKey(13));
        assertEquals(2, game.getPlayer1().getMoney(), "Player 1 money should be 2.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_guildOtherPlayerMoreCards() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setAge(3);
        game.setPyramid(createAgeThreePyramid());
        game.getPyramid().remove(19);
        game.getPyramid().remove(18);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().setMoney(10);
        game.getPlayer1().getHand().addAll(Set.of(CardName.STONE_PIT, CardName.ALTER, CardName.LUMBER_YARD));
        game.getPlayer2().getHand().addAll(Set.of(CardName.CUSTOMS_HOUSE, CardName.DISPENSARY));

        assertEquals(6, CardName.SCIENTISTS_GUILD.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()), "Cost should be 6.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 16);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertEquals(Set.of(CardName.STONE_PIT, CardName.ALTER, CardName.LUMBER_YARD, CardName.SCIENTISTS_GUILD), game.getPlayer1().getHand(), "Player 1 hand should have added SCIENTISTS_GUILD.");
        assertFalse(game.getPyramid().containsKey(16), "Pyramid should not have index 16.");
        assertEquals(5, game.getPlayer1().getMoney(), "Player 1 money should be 5.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_guildShipowners() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.putAll(Map.of(0, CardName.SHIPOWNERS_GUILD,
                1, CardName.TOWNHALL));
        game.setPyramid(pyramid);
        game.setAge(3);
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().getHand().addAll(Set.of(CardName.CLAY_RESERVE, CardName.GARRISON, CardName.LUMBER_YARD, CardName.STONE_PIT, CardName.GLASSBLOWER));
        game.getPlayer1().getHand().addAll(Set.of(CardName.BUILDERS_GUILD));
        game.getPlayer2().setMoney(5);

        assertEquals(3, CardName.SHIPOWNERS_GUILD.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer2().getHand(), game.getPlayer1().getHand()), "Cost should be 3.");


        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 0);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertEquals(Set.of(CardName.CLAY_RESERVE, CardName.GARRISON, CardName.LUMBER_YARD, CardName.STONE_PIT, CardName.GLASSBLOWER, CardName.SHIPOWNERS_GUILD), game.getPlayer2().getHand(), "Player 2 hand should have added SHIPOWNERS_GUILD.");
        assertEquals(5, game.getPlayer2().getMoney(), "PLayer 2 money should be 5.");
        assertFalse(game.getPyramid().containsKey(0), "Pyramid should not contain index 0.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_moneylendersGuild() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.putAll(Map.of(0, CardName.ARENA,
                1, CardName.MONEYLENDERS_GUILD));
        game.setPyramid(pyramid);
        game.setAge(3);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.APOTHECARY, CardName.CLAY_PIT, CardName.SHELF_QUARRY, CardName.CARAVANSERY, CardName.WOOD_RESERVE));
        game.getPlayer2().getHand().addAll(Set.of(CardName.LUMBER_YARD));
        game.getPlayer1().setMoney(20);

        assertEquals(1, CardName.MONEYLENDERS_GUILD.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 1);

        assertEquals(Set.of(CardName.APOTHECARY, CardName.CLAY_PIT, CardName.SHELF_QUARRY, CardName.CARAVANSERY, CardName.WOOD_RESERVE, CardName.MONEYLENDERS_GUILD), game.getPlayer1().getHand(), "Player 1 hand should have added MONEYLENDERS_GUILD.");
        assertEquals(19, game.getPlayer1().getMoney(), "Player 1 money should be 19.");
        assertFalse(game.getPyramid().containsKey(1), "Pyramid should not contain index 1.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_economy() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeThreePyramid());
        game.setAge(3);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.CUSTOMS_HOUSE, CardName.FORUM));
        game.getPlayer2().getHand().addAll(Set.of(CardName.SAWMILL));
        game.getPlayer1().setMoney(10);
        game.getPlayer2().setMoney(5);
        game.getPlayer2().getTokens().add(ProgressToken.ECONOMY);

        assertEquals(7, CardName.PANTHEON.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 18);

        assertEquals(3, game.getPlayer1().getMoney(), "Player 1 money should be 3.");
        assertEquals(12, game.getPlayer2().getMoney(), "PLayer 2 money should be 12.");
        assertEquals(Set.of(CardName.CUSTOMS_HOUSE, CardName.FORUM, CardName.PANTHEON), game.getPlayer1().getHand(), "PLayer 1 hand should include PANTHEON.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_economyFreeBuild() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeThreePyramid());
        game.setAge(3);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.CUSTOMS_HOUSE, CardName.FORUM, CardName.TEMPLE));
        game.getPlayer2().getHand().addAll(Set.of(CardName.SAWMILL));
        game.getPlayer1().setMoney(10);
        game.getPlayer2().setMoney(5);
        game.getPlayer2().getTokens().add(ProgressToken.ECONOMY);

        assertEquals(0, CardName.PANTHEON.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 18);

        assertEquals(10, game.getPlayer1().getMoney());
        assertEquals(5, game.getPlayer2().getMoney());

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_economyHasMoneyCost() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeTwoPyramid());
        game.getPyramid().put(0, CardName.BREWERY);
        game.getPyramid().put(19, CardName.FORUM);
        game.setCurrentPlayerNumber(2);
        game.getPlayer1().getTokens().add(ProgressToken.ECONOMY);
        game.getPlayer2().getHand().addAll(Set.of(CardName.APOTHECARY, CardName.GLASSWORKS));
        game.getPlayer2().setMoney(7);
        game.getPlayer1().setMoney(5);

        assertEquals(5, CardName.FORUM.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer2().getHand(), game.getPlayer1().getHand()), "Cost should be 5.");

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 19);

        assertEquals(2, game.getPlayer2().getMoney(), "Player 2 money should be 2.");
        assertEquals(7, game.getPlayer1().getMoney(), "Player 1 money should be 7.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_masonryBlue() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeThreePyramid());
        game.getPyramid().put(19, CardName.PALACE);
        game.getPyramid().put(10, CardName.ARSENAL);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getTokens().add(ProgressToken.MASONRY);
        game.getPlayer1().getHand().addAll(Set.of(CardName.CUSTOMS_HOUSE, CardName.CARAVANSERY));
        game.getPlayer2().getHand().addAll(Set.of(CardName.BRICKYARD, CardName.QUARRY));
        game.getPlayer1().setMoney(10);

        assertEquals(2, CardName.PALACE.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand(), EnumSet.noneOf(Wonder.class), true));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 19);

        assertEquals(8, game.getPlayer1().getMoney(), "PLayer 1 money should be 9.");
        assertEquals(Set.of(CardName.CUSTOMS_HOUSE, CardName.CARAVANSERY, CardName.PALACE), game.getPlayer1().getHand(), "Player hand should have added ");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_urbanismFreeBuild_endGame() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setAge(3);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.put(0, CardName.OBSERVATORY);
        game.setPyramid(pyramid);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().add(CardName.LABORATORY);
        game.getPlayer1().getTokens().add(ProgressToken.URBANISM);
        game.getPlayer1().setMoney(6);

        assertEquals(0, CardName.OBSERVATORY.getCard().getCost().calculateTotalMonetaryCost(game.getPlayer1().getHand(), game.getPlayer2().getHand()));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuilding("123", 0);

        assertEquals(GameStep.GAME_END, game.getStep(), "Game step should be GAME_END.");
        assertTrue(game.getPyramid().isEmpty(), "Pyramid should be empty.");
        assertEquals(Set.of(CardName.LABORATORY, CardName.OBSERVATORY), game.getPlayer1().getHand(), "Player 1 hnd should have added OBSERVATORY.");
        assertEquals(10, game.getPlayer1().getMoney(), "Player 1 money should be 10.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_wrongStep_throwsInvalidMoveException() throws GameCodeNotFoundException, InvalidMoveException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.DESTROY_GREY);
        game.getPlayer1().setMoney(100);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.constructBuilding("123", 19));

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_indexNotValid_throwsInvalidMoveException() throws GameCodeNotFoundException, InvalidMoveException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeOnePyramid());
        game.getPlayer1().setMoney(100);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.constructBuilding("123", 5));

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructBuilding_notEnoughMoney_throwsInvalidMoveException() {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().setMoney(0);
        game.setPyramid(createAgeOnePyramid());

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.constructBuilding("123", 16));

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructBuildingFromDiscard_military_theology() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setAge(2);
        game.setPyramid(createAgeTwoPyramid());
        game.setStep(GameStep.CONSTRUCT_FROM_DISCARD);
        game.getDiscardedCards().addAll(Set.of(CardName.GARRISON, CardName.APOTHECARY, CardName.ALTER));
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getTokens().add(ProgressToken.THEOLOGY);
        game.getMilitary().setMilitaryPosition(-2);
        game.getPlayer1().setMoney(3);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuildingFromDiscard("123", CardName.GARRISON);

        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertEquals(-1, game.getMilitary().getMilitaryPosition(), "Military position should be -1.");
        assertTrue(game.getPlayer1().getHand().contains(CardName.GARRISON), "Player hand should contain GARRISON.");
        assertFalse(game.getDiscardedCards().contains(CardName.GARRISON), "Discard pile should not contain GARRISON.");
        assertEquals(3, game.getPlayer1().getMoney(), "Player 1 money should be 3.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuildingFromDiscard_science() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setAge(2);
        game.setPyramid(createAgeTwoPyramid());
        game.setStep(GameStep.CONSTRUCT_FROM_DISCARD);
        game.getDiscardedCards().addAll(Set.of(CardName.GARRISON, CardName.APOTHECARY, CardName.ALTER));
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().setMoney(4);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.constructBuildingFromDiscard("123", CardName.APOTHECARY);

        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertTrue(game.getPlayer2().getHand().contains(CardName.APOTHECARY), "Player hand should contain APOTHECARY.");
        assertFalse(game.getDiscardedCards().contains(CardName.APOTHECARY), "Discard pile should not contain APOTHECARY.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void constructBuildingFromDiscard_notInDiscard_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setAge(2);
        game.setPyramid(createAgeTwoPyramid());
        game.setStep(GameStep.CONSTRUCT_FROM_DISCARD);
        game.getDiscardedCards().addAll(Set.of(CardName.GARRISON, CardName.ALTER));
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().setMoney(4);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.constructBuildingFromDiscard("123", CardName.APOTHECARY), "Should throw InvalidMoveException.");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void constructBuildingFromDiscard_wrongStep_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getDiscardedCards().addAll(Set.of(CardName.GARRISON, CardName.ALTER));

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.constructBuildingFromDiscard("123", CardName.ALTER), "Should throw InvalidMoveException.");

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

        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN);

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
    public void chooseProgressToken_startNewAge() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(new HashMap<Integer, CardName>());
        game.setAge(1);
        game.setCurrentPlayerNumber(2);
        game.getPlayer2().setMoney(3);
        game.getTokensAvailable().clear();
        game.getTokensAvailable().addAll(Set.of(ProgressToken.MASONRY, ProgressToken.ARCHITECTURE));
        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.chooseProgressToken("123", ProgressToken.MASONRY);

        assertEquals(2, game.getAge(), "Age should be 2.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player number should be 1.");
        assertEquals(Set.of(ProgressToken.ARCHITECTURE), game.getTokensAvailable(), "Tokens available should be ARCHITECTURE.");
        assertEquals(3, game.getPlayer2().getMoney(), "Money should be 3.");
        assertPyramidContainsIndexesUpTo(game.getPyramid(), 19);
        assertAllCardsInAge(game.getPyramid(), 2);

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

        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN);

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


        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN);

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

        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN);

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

        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN);

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

    @Test
    public void discard_noCommercialBuildings_increaseMoneyTwo() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer1().getHand().addAll(Set.of(CardName.THEATRE, CardName.STONE_PIT, CardName.PALISADE));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STONE_RESERVE));
        game.setCurrentPlayerNumber(1);
        game.getMilitary().setMilitaryPosition(-1);
        game.setPyramid(createAgeTwoPyramid());
        game.setAge(2);
        game.getPlayer1().setMoney(3);
        game.getPlayer2().setMoney(2);
        game.getDiscardedCards().addAll(Set.of(CardName.WORKSHOP, CardName.GLASSWORKS, CardName.PRESS));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.discard("123", 18);

        assertEquals(Set.of(CardName.THEATRE, CardName.STONE_PIT, CardName.PALISADE), game.getPlayer1().getHand(), "Player hand should be the same after discard.");
        assertEquals(5, game.getPlayer1().getMoney(), "Player money should increase from 3 to 5.");
        assertEquals(2, game.getPlayer2().getMoney(), "Player money should remain at 2.");
        assertEquals(Set.of(CardName.WORKSHOP, CardName.GLASSWORKS, CardName.PRESS, CardName.BARRACKS), game.getDiscardedCards(), "Discarded cards should have added BARRACKS.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player should be 2.");
        assertFalse(game.getPyramid().containsKey(18), "Pyramid should not contain index 18.");
        assertEquals(-1, game.getMilitary().getMilitaryPosition(), "Military position should remain at -1.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void discard_commercialBuildings_increaseMoneyFive() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer1().getHand().addAll(Set.of(CardName.THEATRE, CardName.STONE_PIT, CardName.PALISADE));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STONE_RESERVE, CardName.CLAY_RESERVE, CardName.TAVERN, CardName.SCRIPTORIUM));
        game.setCurrentPlayerNumber(2);
        game.getMilitary().setMilitaryPosition(-1);
        game.setPyramid(createAgeTwoPyramid());
        game.setAge(2);
        game.getPyramid().remove(19);
        game.getPyramid().remove(18);
        game.getPyramid().remove(17);
        game.getPyramid().remove(14);
        game.getPlayer1().setMoney(3);
        game.getPlayer2().setMoney(2);
        game.getDiscardedCards().addAll(Set.of(CardName.WORKSHOP, CardName.GLASSWORKS, CardName.PRESS));

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.discard("123", 15);

        assertEquals(Set.of(CardName.STONE_RESERVE, CardName.CLAY_RESERVE, CardName.TAVERN, CardName.SCRIPTORIUM), game.getPlayer2().getHand(), "PLayer 2 hand should remain the same.");
        assertEquals(3, game.getPlayer1().getMoney(), "Player 1 money should remain at 3.");
        assertEquals(7, game.getPlayer2().getMoney(), "Player 2 money should increase from 2 to 7.");
        assertEquals(Set.of(CardName.WORKSHOP, CardName.GLASSWORKS, CardName.PRESS, CardName.BRICKYARD), game.getDiscardedCards(), "Discarded cards should have added BRICKYARD.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player should be 1.");
        assertFalse(game.getPyramid().containsKey(15), "Pyramid should not contain index 15.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void discard_lastCardInPyramid_changeAge() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer1().getHand().addAll(Set.of(CardName.THEATRE, CardName.STONE_PIT, CardName.PALISADE));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STONE_RESERVE, CardName.SCRIPTORIUM));
        game.setCurrentPlayerNumber(2);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.put(0, CardName.CLAY_PIT);
        game.setPyramid(pyramid);
        game.getPlayer1().setMoney(9);
        game.getPlayer2().setMoney(0);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.discard("123", 0);

        assertEquals(Set.of(CardName.STONE_RESERVE, CardName.SCRIPTORIUM), game.getPlayer2().getHand(), "PLayer 2 hand should remain the same.");
        assertEquals(9, game.getPlayer1().getMoney(), "Player 1 money should remain at 9.");
        assertEquals(3, game.getPlayer2().getMoney(), "Player 2 money should increase from 0 to 3.");
        assertEquals(Set.of(CardName.CLAY_PIT), game.getDiscardedCards(), "Discarded cards should have added CLAY_PIT.");
        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(1, game.getCurrentPlayerNumber(), "Current player should be 1.");
        assertEquals(2, game.getAge(), "Age should be 2.");
        assertAllCardsInAge(game.getPyramid(), 2);
        assertPyramidContainsIndexesUpTo(game.getPyramid(), 19);

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void discard_lastCardInPyramidAgeThree_endGame() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.getPlayer1().getHand().addAll(Set.of(CardName.THEATRE, CardName.STONE_PIT, CardName.PALISADE));
        game.getPlayer2().getHand().addAll(Set.of(CardName.STONE_RESERVE, CardName.SCRIPTORIUM));
        game.setCurrentPlayerNumber(1);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.put(0, CardName.MONEYLENDERS_GUILD);
        game.setPyramid(pyramid);
        game.setAge(3);
        game.getPlayer1().setMoney(1);
        game.getPlayer2().setMoney(1);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.discard("123", 0);

        assertEquals(Set.of(CardName.THEATRE, CardName.STONE_PIT, CardName.PALISADE), game.getPlayer1().getHand(), "PLayer 1 hand should remain the same.");
        assertEquals(3, game.getPlayer1().getMoney(), "Player 1 money should increase from 1 to 3.");
        assertEquals(1, game.getPlayer2().getMoney(), "Player 2 money should remain at 1.");
        assertEquals(Set.of(CardName.MONEYLENDERS_GUILD), game.getDiscardedCards(), "Discarded cards should have added MONEYLENDERS_GUILD.");
        assertEquals(GameStep.GAME_END, game.getStep(), "Game step should be GAME_END.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void discard_indexNotInPyramid_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeOnePyramid());
        game.getPyramid().remove(17);
        game.getDiscardedCards().add(CardName.CLAY_PIT);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.discard("123", 17), "Should throw InvalidMoveException");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void discard_indexNotActive_throwsInvalidMoveException() {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.PLAY_CARD);
        game.setPyramid(createAgeOnePyramid());
        game.getDiscardedCards().add(CardName.CLAY_PIT);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.discard("123", 6), "Should throw InvalidMoveException");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void destroyCard_brown() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.ALTER));
        game.getPlayer2().getHand().addAll(Set.of(CardName.QUARRY, CardName.DISPENSARY));
        game.setStep(GameStep.DESTROY_BROWN);
        game.getDiscardedCards().add(CardName.THEATRE);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.destroyCard("123", CardName.QUARRY);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertEquals(Set.of(CardName.ALTER), game.getPlayer1().getHand(), "Player 1 hand should not change.");
        assertEquals(Set.of(CardName.DISPENSARY), game.getPlayer2().getHand(), "Player 2 should only have DISPENSARY.");
        assertEquals(Set.of(CardName.THEATRE, CardName.QUARRY), game.getDiscardedCards(), "Discard pile should have added QUARRY.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void destroyCard_cardMismatch_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.ALTER));
        game.getPlayer2().getHand().addAll(Set.of(CardName.QUARRY, CardName.DISPENSARY));
        game.setStep(GameStep.DESTROY_GREY);
        game.getDiscardedCards().add(CardName.THEATRE);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.destroyCard("123", CardName.DISPENSARY), "Should throw InvalidMoveException.");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void destroyCard_incorrectStep_throwsInvalidMoveException() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getHand().addAll(Set.of(CardName.ALTER));
        game.getPlayer2().getHand().addAll(Set.of(CardName.QUARRY, CardName.DISPENSARY));
        game.setStep(GameStep.PLAY_CARD);
        game.getDiscardedCards().add(CardName.THEATRE);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.destroyCard("123", CardName.QUARRY));

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void destroyCard_cardNotInOpponentHand() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setCurrentPlayerNumber(2);
        game.getPlayer1().getHand().addAll(Set.of(CardName.ALTER));
        game.getPlayer2().getHand().addAll(Set.of(CardName.QUARRY, CardName.DISPENSARY));
        game.setStep(GameStep.DESTROY_BROWN);
        game.getDiscardedCards().add(CardName.THEATRE);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.destroyCard("123", CardName.QUARRY), "Should throw InvalidMoveException");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void chooseDiscardedScience_valid() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN_FROM_DISCARD);
        game.setCurrentPlayerNumber(1);
        game.getTokensFromUnavailable().addAll(Set.of(ProgressToken.MASONRY, ProgressToken.URBANISM, ProgressToken.AGRICULTURE));
        game.getPlayer1().setMoney(5);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.chooseProgressTokenFromDiscard("123", ProgressToken.AGRICULTURE);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertEquals(11, game.getPlayer1().getMoney(), "Player 1 money should be 11.");
        assertTrue(game.getPlayer1().getTokens().contains(ProgressToken.AGRICULTURE), "PLayer 1 should have AGRICULTURE token.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void chooseDiscardedScience_theology() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN_FROM_DISCARD);
        game.setCurrentPlayerNumber(2);
        game.getTokensFromUnavailable().addAll(Set.of(ProgressToken.MASONRY, ProgressToken.URBANISM, ProgressToken.AGRICULTURE));
        game.getPlayer2().setMoney(5);
        game.getPlayer2().getTokens().add(ProgressToken.THEOLOGY);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.chooseProgressTokenFromDiscard("123", ProgressToken.URBANISM);

        assertEquals(GameStep.PLAY_CARD, game.getStep(), "Game step should be PLAY_CARD.");
        assertEquals(2, game.getCurrentPlayerNumber(), "Current player number should be 2.");
        assertEquals(11, game.getPlayer2().getMoney(), "Player 2 money should be 11.");
        assertTrue(game.getPlayer1().getTokens().add(ProgressToken.URBANISM), "PLayer 1 should have URBANISM token.");

        verify(gameDao, times(2)).save(game);
        verify(playerDao, times(4)).save(any(Player.class));
        verify(militaryDao, times(2)).save(game.getMilitary());
    }

    @Test
    public void chooseDiscardedScience_wrongStep() {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN);
        game.setCurrentPlayerNumber(2);
        game.getTokensFromUnavailable().addAll(Set.of(ProgressToken.MASONRY, ProgressToken.URBANISM, ProgressToken.AGRICULTURE));

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.chooseProgressTokenFromDiscard("123", ProgressToken.MASONRY), "Should throw InvalidMoveException.");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void chooseDiscardedScience_tokenUnavailable() {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN_FROM_DISCARD);
        game.setCurrentPlayerNumber(1);
        game.getTokensFromUnavailable().addAll(Set.of(ProgressToken.MASONRY, ProgressToken.URBANISM, ProgressToken.AGRICULTURE));

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.chooseProgressTokenFromDiscard("123", ProgressToken.THEOLOGY), "Should throw InvalidMoveException.");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
    }

    @Test
    public void discard_gameEnd() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();
        game.setStep(GameStep.PLAY_CARD);
        game.setCurrentPlayerNumber(1);

        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        player1.getHand().addAll(Set.of(CardName.ALTER, CardName.TEMPLE, CardName.PANTHEON, CardName.THEATRE,
                CardName.SENATE, CardName.AQUEDUCT, CardName.BATHS, CardName.COURTHOUSE,
                CardName.GLASSWORKS, CardName.PRESS, CardName.DRYING_ROOM, CardName.OBSERVATORY,
                CardName.GLASSBLOWER, CardName.HORSE_BREEDERS, CardName.PARADE_GROUND,
                CardName.FORUM, CardName.CARAVANSERY, CardName.LIGHTHOUSE, CardName.ARENA,
                CardName.ACADEMY, CardName.STUDY, CardName.SCRIPTORIUM,
                CardName.APOTHECARY, CardName.WORKSHOP, CardName.ARCHERY_RANGE,
                CardName.STABLE, CardName.BARRACKS, CardName.CIRCUS, CardName.UNIVERSITY,
                CardName.ARSENAL, CardName.WALLS));

        player2.getHand().addAll(Set.of(CardName.GUARD_TOWER, CardName.GARRISON,
                CardName.LUMBER_YARD, CardName.STONE_PIT, CardName.CLAY_POOL,
                CardName.STATUE, CardName.GARDENS, CardName.PALACE,
                CardName.BRICKYARD, CardName.QUARRY,
                CardName.POSTRUM, CardName.SAWMILL, CardName.SHELF_QUARRY,
                CardName.ARMORY, CardName.CHAMBER_OF_COMMERCE,
                CardName.LABORATORY, CardName.LIBRARY, CardName.SCHOOL,
                CardName.PORT, CardName.PRETORIUM, CardName.TOWNHALL, CardName.FORTIFICATIONS,
                CardName.BUILDERS_GUILD, CardName.MAGISTRATES_GUILD, CardName.SIEGE_WORKSHOP,
                CardName.MERCHANTS_GUILD, CardName.MONEYLENDERS_GUILD));

        player1.getTokens().addAll(Set.of(ProgressToken.ARCHITECTURE, ProgressToken.ECONOMY));

        player1.setWonders(Map.of(Wonder.THE_COLOSSUS, 1,
                Wonder.THE_APPIAN_WAY, 0,
                Wonder.THE_HANGING_GARDENS, 3,
                Wonder.THE_GREAT_LIGHTHOUSE, 0));

        player2.getTokens().addAll(Set.of(ProgressToken.LAW));

        player2.setWonders(Map.of(Wonder.THE_MAUSOLEUM, 1,
                Wonder.THE_PYRAMIDS, 0,
                Wonder.THE_TEMPLE_OF_ARTEMIS, 3,
                Wonder.THE_STATUE_OF_ZEUS,0));

        player1.setMoney(4); //gets 6 coins for discarding
        player2.setMoney(13);

        game.getMilitary().setMilitaryPosition(7);
        game.setAge(3);
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.putAll(Map.of(0, CardName.OBELISK));
        game.setPyramid(pyramid);

        when(gameDao.findByCode("123")).thenReturn(game);

        gameService.discard("123", 0);

        assertEquals(GameStep.GAME_END, game.getStep(), "Game step should be GAME_END.");
        assertEquals(34 ,game.getPlayer1().getScore().get(CardOrValueType.CIVILIAN_BUILDING), "Player 1 should have 34 points for CIVILIAN_BUILDING.");
        assertEquals(12, game.getPlayer1().getScore().get(CardOrValueType.SCIENTIFIC_BUILDING), "Player 1 should have 12 points for SCIENTIFIC_BUILDING.");
        assertEquals(6, game.getPlayer1().getScore().get(CardOrValueType.COMMERCIAL_BUILDING), "Player 1 should have 6 points for COMMERCIAL_BUILDING.");
        assertEquals(0, game.getPlayer1().getScore().get(CardOrValueType.GUILD), "Player 1 should have 0 points for GUILD.");
        assertEquals(10, game.getPlayer1().getScore().get(CardOrValueType.MILITARY_BUILDING), "Player 1 should have 10 points for MILITARY_BUILDING.");
        assertEquals(6, game.getPlayer1().getScore().get(CardOrValueType.WONDER), "Player 1 should have 6 points for WONDER.");
        assertEquals(3, game.getPlayer1().getScore().get(CardOrValueType.MONEY), "Player 1 should have 1 point for MONEY.");
        assertEquals(0, game.getPlayer1().getScore().get(CardOrValueType.PROGRESS_TOKEN), "Player 1 should have 0 points for PROGRESS_TOKEN.");
        assertEquals(28, game.getPlayer2().getScore().get(CardOrValueType.CIVILIAN_BUILDING), "Player 2 should have 28 points for CIVILIAN_BUILDING.");
        assertEquals(4, game.getPlayer2().getScore().get(CardOrValueType.SCIENTIFIC_BUILDING), "Player 2 should have 4 points for SCIENTIFIC_BUILDING.");
        assertEquals(9, game.getPlayer2().getScore().get(CardOrValueType.COMMERCIAL_BUILDING), "Player 2 should have 9 points for COMMERCIAL_BUILDING.");
        assertEquals(20, game.getPlayer2().getScore().get(CardOrValueType.GUILD), "Player 2 should have 20 points for GUILD.");
        assertEquals(0, game.getPlayer2().getScore().get(CardOrValueType.MILITARY_BUILDING), "Player 2 should have 0 points for MILITARY_BUILDING.");
        assertEquals(2, game.getPlayer2().getScore().get(CardOrValueType.WONDER), "Player 2 should have 2 points for WONDER.");
        assertEquals(4, game.getPlayer2().getScore().get(CardOrValueType.MONEY), "Player 2 should have 4 points for MONEY.");
        assertEquals(0, game.getPlayer2().getScore().get(CardOrValueType.PROGRESS_TOKEN), "Player 2 should have 0 points for PROGRESS_TOKEN.");
        assertTrue(game.getPlayer1().getWon(), "Player 1 should have won.");
        assertFalse(game.getPlayer2().getWon(), "Player 2 should not have won.");

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
        assertTrue(player.getSortedHand().isEmpty(), "Player sorted hand should be empty");
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

    private Map<Integer, CardName> createAgeOnePyramid() {
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.putAll(Map.of(0, CardName.BATHS,
                1, CardName.CLAY_POOL,
                2, CardName.WOOD_RESERVE,
                3, CardName.GUARD_TOWER,
                4, CardName.TAVERN,
                5, CardName.STONE_RESERVE,
                6, CardName.LUMBER_YARD,
                7, CardName.PHARMACIST,
                8, CardName.STABLE,
                9, CardName.ALTER));
        pyramid.putAll(Map.of(10, CardName.THEATRE,
                11, CardName.LOGGING_CAMP,
                12, CardName.SCRIPTORIUM,
                13, CardName.GARRISON,
                14, CardName.WORKSHOP,
                15, CardName.GLASSWORKS,
                16, CardName.PRESS,
                17, CardName.CLAY_PIT,
                18, CardName.QUARRY,
                19, CardName.APOTHECARY));
        return pyramid;
    }

    public Map<Integer, CardName> createAgeTwoPyramid() {
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.putAll(Map.of(0, CardName.FORUM,
                1, CardName.STATUE,
                2, CardName.CUSTOMS_HOUSE,
                3, CardName.SHELF_QUARRY,
                4, CardName.HORSE_BREEDERS,
                5, CardName.POSTRUM,
                6, CardName.TEMPLE,
                7, CardName.SAWMILL,
                8, CardName.LABORATORY,
                9, CardName.DISPENSARY));
        pyramid.putAll(Map.of(10, CardName.LIBRARY,
                11, CardName.SCHOOL,
                12, CardName.AQUEDUCT,
                13, CardName.PARADE_GROUND,
                14, CardName.WALLS,
                15, CardName.BRICKYARD,
                16, CardName.ARCHERY_RANGE,
                17, CardName.DRYING_ROOM,
                18, CardName.BARRACKS,
                19, CardName.BREWERY));
        return pyramid;
    }

    public Map<Integer, CardName> createAgeThreePyramid() {
        Map<Integer, CardName> pyramid = new HashMap<>();
        pyramid.putAll(Map.of(0, CardName.LIGHTHOUSE,
                1, CardName.GARDENS,
                2, CardName.SENATE,
                3, CardName.MONEYLENDERS_GUILD,
                4, CardName.CHAMBER_OF_COMMERCE,
                5, CardName.TOWNHALL,
                6, CardName.SIEGE_WORKSHOP,
                7, CardName.PRETORIUM,
                8, CardName.OBELISK,
                9, CardName.PORT));
        pyramid.putAll(Map.of(
                10, CardName.PALACE,
                11, CardName.CIRCUS,
                12, CardName.ARENA,
                13, CardName.MAGISTRATES_GUILD,
                14, CardName.STUDY,
                15, CardName.OBSERVATORY,
                16, CardName.SCIENTISTS_GUILD,
                17, CardName.ARMORY,
                18, CardName.PANTHEON,
                19, CardName.ARSENAL));
        return pyramid;
    }

}
