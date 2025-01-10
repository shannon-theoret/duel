package com.shannontheoret.duel.service;

import com.shannontheoret.duel.CardDTO;
import com.shannontheoret.duel.GameStep;
import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.card.*;
import com.shannontheoret.duel.dao.GameDao;
import com.shannontheoret.duel.dao.MilitaryDao;
import com.shannontheoret.duel.dao.PlayerDao;
import com.shannontheoret.duel.entity.Game;
import com.shannontheoret.duel.entity.Military;
import com.shannontheoret.duel.entity.Player;
import com.shannontheoret.duel.exceptions.GameCodeNotFoundException;
import com.shannontheoret.duel.exceptions.InvalidMoveException;
import com.shannontheoret.duel.utility.HandUtility;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private GameDao gameDao;
    private PlayerDao playerDao;

    private MilitaryDao militaryDao;

    @Autowired
    public GameService(GameDao gameDao, PlayerDao playerDao, MilitaryDao militaryDao) {
        this.gameDao = gameDao;
        this.playerDao = playerDao;
        this.militaryDao = militaryDao;
    }

    @Transactional
    public Game findByCode(String code)  throws GameCodeNotFoundException {
        Game game = gameDao.findByCode(code);
        if (game==null) {
            throw new GameCodeNotFoundException(code);
        }
        return game;
    }

    @Transactional
    public Game newGame() {
        Game game = new Game();
        game.setCode(generateCode());
        game.setAge(1);
        game.setCurrentPlayerNumber(1);
        game.setStep(GameStep.PLAY_CARD);
        Player player1 = new Player();
        player1.setMoney(7);
        player1.setHand(new HashSet<>());
        Player player2 = new Player();
        player2.setMoney(7);
        player2.setHand(new HashSet<>());
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setMilitary(new Military());
        List<ProgressToken> allTokens = new ArrayList<>(List.of(ProgressToken.values()));
        Collections.shuffle(allTokens);
        EnumSet<ProgressToken> tokensAvailable = EnumSet.noneOf(ProgressToken.class);
        tokensAvailable.addAll(allTokens.subList(0,5));
        game.setTokensAvailable(tokensAvailable);
        EnumSet<ProgressToken> tokensUnavailable = EnumSet.noneOf(ProgressToken.class);
        tokensUnavailable.addAll(allTokens.subList(5,10));
        game.setTokensUnavailable(tokensUnavailable);
        game.setPyramid(generateStartingPyramid());

        save(game);
        return game;
    }

    @Transactional
    public Game constructBuilding(String code, Integer cardIndex) throws GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        confirmCorrectStep(game, GameStep.PLAY_CARD);
        Player player = game.findActivePlayer();
        Player opponent = game.findNonActivePlayer();
        confirmCardInVisiblePyramid(game, cardIndex);
        CardName cardName = game.getPyramid().get(cardIndex);
        Boolean buildWithTwoFewer = (player.getTokens().contains(ProgressToken.MASONRY) && cardName.getCard().getCardType() == CardOrValueType.CIVILIAN_BUILDING);
        Integer totalMonetaryCost = cardName.getCard().getCost().calculateTotalMonetaryCost(player.getHand(), game.findNonActivePlayer().getHand(), buildWithTwoFewer);
        if (totalMonetaryCost > player.getMoney()) {
            throw new InvalidMoveException("Player does not have enough money to make purchase");
        }
        if (opponent.getTokens().contains(ProgressToken.ECONOMY)) {
            Integer totalTradeCost = totalMonetaryCost - cardName.getCard().getCost().getMonetaryCost();
            opponent.setMoney(opponent.getMoney() + totalTradeCost);
        }
        player.setMoney(player.getMoney() - totalMonetaryCost);
        Integer monetaryGain = 0;
        if (player.getTokens().contains(ProgressToken.URBANISM) && cardName.getCard().getCost().constructForFree(player.getHand())) {
            monetaryGain += 4;
        }
        switch (cardName.getCard().getCardType()) {
            case COMMERCIAL_BUILDING:
                CommercialBuildingCard commercialBuildingCard = (CommercialBuildingCard) cardName.getCard();
                if (commercialBuildingCard.getMoney() > 0) {
                    if (commercialBuildingCard.getMoneyPerType() != null) {
                        if (commercialBuildingCard.getMoneyPerType() == CardOrValueType.WONDER) {
                            //TODO: calculate
                        } else {
                            monetaryGain = HandUtility.countNumberOfCardType(commercialBuildingCard.getMoneyPerType(), player.getHand()) * commercialBuildingCard.getMoney();
                        }
                    } else {
                        monetaryGain = commercialBuildingCard.getMoney();
                    }
                }
                break;
            case GUILD:
                GuildCard guildCard = (GuildCard) cardName.getCard();
                switch (guildCard.getCardOrValueTypeForVictoryPoints()) {
                    case MONEY:
                        monetaryGain = guildCard.getMoneyPerValueType();
                        break;
                    case WONDER:
                        //TODO calculate
                        break;
                    case RAW_MATERIAL_AND_MANUFACTURED_GOOD:
                        Integer opponentCount = HandUtility.countNumberOfCardType(CardOrValueType.RAW_MATERIAL, game.findNonActivePlayer().getHand())
                                + HandUtility.countNumberOfCardType(CardOrValueType.MANUFACTURED_GOOD, game.findNonActivePlayer().getHand());
                        Integer playerCount = HandUtility.countNumberOfCardType(CardOrValueType.RAW_MATERIAL, player.getHand())
                                + HandUtility.countNumberOfCardType(CardOrValueType.MANUFACTURED_GOOD, player.getHand());
                        Integer maxCount = Math.max(opponentCount, playerCount);
                        monetaryGain = maxCount * guildCard.getMoneyPerValueType();
                        break;
                    default:
                        Integer otherPlayerCount = HandUtility.countNumberOfCardType(guildCard.getCardOrValueTypeForVictoryPoints(), game.findNonActivePlayer().getHand());
                        Integer currentPlayerCount = HandUtility.countNumberOfCardType(guildCard.getCardOrValueTypeForVictoryPoints(), player.getHand());
                        Integer maxCardCount = Math.max(otherPlayerCount, currentPlayerCount);
                        monetaryGain = maxCardCount * guildCard.getMoneyPerValueType();
                        break;
                }
                break;
            case MILITARY_BUILDING:
                MilitaryBuildingCard militaryBuildingCard = (MilitaryBuildingCard) cardName.getCard();
                Integer militaryGain = militaryBuildingCard.getMilitaryGain();
                if (player.getTokens().contains(ProgressToken.STRATEGY)) {
                    militaryGain += 1;
                }
                if (game.getCurrentPlayerNumber() == 1) {
                    game.getMilitary().setMilitaryPosition(game.getMilitary().getMilitaryPosition()+ militaryGain);
                } else {
                    game.getMilitary().setMilitaryPosition(game.getMilitary().getMilitaryPosition() - militaryGain);
                }
                game.applyMilitaryEffect();
                break;
            case SCIENTIFIC_BUILDING:
                ScientificBuildingCard scientificBuildingCard = (ScientificBuildingCard) cardName.getCard();
                if (player.checkNewScienceMatch(scientificBuildingCard.getScienceSymbol())) {
                    game.setStep(GameStep.CHOOSE_SCIENCE);
                }
                break;
            default:
                break;
        }
        player.setMoney(player.getMoney() + monetaryGain);
        game.getPyramid().remove(cardIndex);
        player.getHand().add(cardName);
        if (game.getStep() == GameStep.PLAY_CARD) {
            endTurn(game);
        }
        save(game);
        return game;
    }

    @Transactional
    public Game chooseProgressToken(String code, ProgressToken progressToken) throws GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        confirmCorrectStep(game, GameStep.CHOOSE_SCIENCE);
        if (!game.getTokensAvailable().contains(progressToken)) {
            throw new InvalidMoveException("Token not available to be chosen");
        }
        progressEffects(game, progressToken);
        game.getTokensAvailable().remove(progressToken);
        endTurn(game);
        save(game);
        return game;
    }

    @Transactional
    public Game discard(String code, Integer cardIndex) throws GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        confirmCorrectStep(game, GameStep.PLAY_CARD);
        Player player = game.findActivePlayer();
        confirmCardInVisiblePyramid(game, cardIndex);
        Integer countCommercial = (int) player.getHand().stream()
                .filter(card -> card.getCard().getCardType() == CardOrValueType.COMMERCIAL_BUILDING).count();
        Integer monetaryGain = 2 + countCommercial;
        player.setMoney(player.getMoney() + monetaryGain);
        CardName discardedCardName = game.getPyramid().get(cardIndex);
        game.getPyramid().remove(cardIndex);
        game.getDiscardedCards().add(discardedCardName);
        endTurn(game);
        save(game);
        return game;
    }

    //TODO:removeme
    @Transactional
    public Game testStuff(String code) throws  GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        game.setPyramid(generateAgeTwoPyramid());
        save(game);
        return game;
    }

    @Transactional
    private void save(Game game) {
        playerDao.save(game.getPlayer1());
        playerDao.save(game.getPlayer2());
        militaryDao.save(game.getMilitary());
        gameDao.save(game);
    }

    private static String generateCode() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Integer STRING_LENGTH = 8;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(STRING_LENGTH);

        for (int i = 0; i < STRING_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    private static Map<Integer, CardName> generateStartingPyramid() {
        List<CardName> shuffledCards = new ArrayList<>(CardName.getAgeOne());
        Collections.shuffle(shuffledCards);

        Map<Integer, CardName> startingPyramid = new HashMap<>();
        for (int i=0; i < 20; i++) {
            startingPyramid.put(i, shuffledCards.get(i));
        }
        return startingPyramid;
    }

    private static Map<Integer, CardName> generateAgeTwoPyramid() {
        List<CardName> shuffledCards = new ArrayList<>(CardName.getAgeTwo());
        Collections.shuffle(shuffledCards);
        Map<Integer, CardName> ageTwoPyramid = new HashMap<>();
        for (int i=0; i < 20; i++) {
            ageTwoPyramid.put(i, shuffledCards.get(i));
        }
        return ageTwoPyramid;
    }

    private static Map<Integer, CardName> generateAgeThreePyramid() {
        List<CardName> ageThreeCards = new ArrayList<>(CardName.getAgeThreeNonGuild());
        Collections.shuffle(ageThreeCards);
        List<CardName> guildCards = new ArrayList<>(CardName.getGuilds());
        Collections.shuffle(guildCards);
        List<CardName> allAgeThree = new ArrayList<>(20);
        allAgeThree.addAll(ageThreeCards.subList(0,17));
        allAgeThree.addAll(guildCards.subList(0,3));
        Collections.shuffle((allAgeThree));
        Map<Integer, CardName> ageThreePyramid = new HashMap<>();
        for (int i=0; i < 20; i++) {
            ageThreePyramid.put(i, allAgeThree.get(i));
        }
        return ageThreePyramid;
    }

    private static void endTurn(Game game) throws InvalidMoveException {
        if (game.getCurrentPlayerNumber() == 1) {
            game.setCurrentPlayerNumber(2);
        } else {
            game.setCurrentPlayerNumber(1);
        }
        game.setStep(GameStep.PLAY_CARD);
        if (game.getPyramid().isEmpty()) {
            switch (game.getAge()) {
                case 1:
                    game.setAge(2);
                    game.setPyramid(generateAgeTwoPyramid());
                    break;
                case 2:
                    game.setAge(3);
                    game.setPyramid(generateAgeThreePyramid());
                    break;
                case 3:
                    game.setStep(GameStep.GAME_END);
                    break;
                default:
                    throw new InvalidMoveException("Age is not valid");
            }
        }
        //todo: calculate winner
    }

    private static void confirmCardInVisiblePyramid(Game game, Integer cardIndex) throws InvalidMoveException {
        Map<Integer, CardDTO> visiblePyramid = game.getVisiblePyramid();
        if (!visiblePyramid.containsKey(cardIndex)) {
            throw new InvalidMoveException("Card not in pyramid");
        }
        if (!visiblePyramid.get(cardIndex).getIsActive()) {
            throw new InvalidMoveException("Card is not accessible");
        }
    }

    private static void progressEffects(Game game, ProgressToken progressToken) throws InvalidMoveException {
        Player player = game.findActivePlayer();
        switch (progressToken) {
            case AGRICULTURE:
                player.setMoney(player.getMoney() + 6);
                break;
            case URBANISM:
                player.setMoney(player.getMoney() + 6);
                break;
            default:
                break;
        }
        player.getTokens().add(progressToken);
        if (player.checkScienceVictory()) {
            game.setStep(GameStep.GAME_END);
            player.setWon(true);
        }
    }

    private static void confirmCorrectStep(Game game, GameStep expectedStep) throws InvalidMoveException {
        if (game.getStep() != expectedStep) {
            throw new InvalidMoveException("Incorrect game step for this move");
        }
    }

}
