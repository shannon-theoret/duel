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

import java.util.HashMap;
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
    public void constructBuilding_commercialBuilding() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

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
    public void constructBuilding_militaryWithStrategyToken() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

        game.setPyramid(createAgeOnePyramid());
        game.getPyramid().remove(19);
        game.getPyramid().remove(18);
        game.setCurrentPlayerNumber(1);
        game.getPlayer1().getTokens().addAll(Set.of(ProgressToken.STRATEGY));
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

    @Test
    public void discard_noCommercialBuildings_increaseMoneyTwo() throws InvalidMoveException, GameCodeNotFoundException {
        doNothing().when(gameDao).save(any(Game.class));
        doNothing().when(playerDao).save(any(Player.class));
        doNothing().when(militaryDao).save(any(Military.class));

        Game game = gameService.newGame();

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

        game.setPyramid(createAgeOnePyramid());
        game.getDiscardedCards().add(CardName.CLAY_PIT);

        when(gameDao.findByCode("123")).thenReturn(game);

        assertThrows(InvalidMoveException.class, () -> gameService.discard("123", 6), "Should throw InvalidMoveException");

        verify(gameDao, times(1)).save(game);
        verify(playerDao, times(2)).save(any(Player.class));
        verify(militaryDao, times(1)).save(game.getMilitary());
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
