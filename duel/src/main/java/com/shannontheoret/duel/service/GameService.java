package com.shannontheoret.duel.service;

import com.shannontheoret.duel.CardDTO;
import com.shannontheoret.duel.GameStep;
import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.Wonder;
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
import com.shannontheoret.duel.utility.ScoreUtility;
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
        game.setStep(GameStep.WONDER_SELECTION);
        List<Wonder> allWonders = new ArrayList<>(List.of(Wonder.values()));
        Collections.shuffle(allWonders);
        EnumSet<Wonder> wondersAvailable = EnumSet.noneOf(Wonder.class);
        wondersAvailable.addAll(allWonders.subList(0, 4));
        game.setWondersAvailable(wondersAvailable);
        EnumSet<Wonder> wondersUnavailable = EnumSet.noneOf(Wonder.class);
        wondersUnavailable.addAll(allWonders.subList(4, 12));
        game.setWondersUnavailable(wondersUnavailable);
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
    public Game selectWonder(String code, Wonder wonder) throws GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        confirmCorrectStep(game, GameStep.WONDER_SELECTION);
        if (!game.getWondersAvailable().contains(wonder)) {
            throw new InvalidMoveException("Wonder not available for selection.");
        }
        Player player = game.findActivePlayer();
        player.selectWonder(wonder);
        game.getWondersAvailable().remove(wonder);
        if (game.getWondersAvailable().size() == 3) {
            game.changeCurrentPlayer();
            //if 2 wonders available player remains the same
        } else if (game.getWondersAvailable().size() == 1) {
            Wonder lastWonder = game.getWondersAvailable().iterator().next();
            game.findNonActivePlayer().selectWonder(lastWonder);
            game.getWondersAvailable().remove(lastWonder);
            if (game.getWondersUnavailable().size() == 8) {
                List<Wonder> remainingWonders = new ArrayList<>(game.getWondersUnavailable());
                Collections.shuffle(remainingWonders);
                game.getWondersAvailable().addAll(remainingWonders.subList(0,4));
                game.getWondersUnavailable().clear();
                game.getWondersUnavailable().addAll(remainingWonders.subList(4,8));
            } else { //8 wonders have been selected
                game.setStep(GameStep.PLAY_CARD);
                game.setCurrentPlayerNumber(1);
            }
        }
        save(game);
        return game;
    }

    @Transactional
    public Game constructWonder(String code, Integer cardIndex, Wonder wonder) throws GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        confirmCorrectStep(game, GameStep.PLAY_CARD);
        Player player = game.findActivePlayer();
        confirmCardInVisiblePyramid(game, cardIndex);
        if (!player.getWonders().containsKey(wonder)) {
            throw new InvalidMoveException("Wonder not available to construct.");
        }
        if (player.hasWonder(wonder)) {
            throw new InvalidMoveException("Wonder is already constructed.");
        }
        if ((player.calculateWondersConstructed().size() + game.findNonActivePlayer().calculateWondersConstructed().size()) == 7) {
            throw new InvalidMoveException("A maximum of seven wonders can be constructed.");
        }
        Integer totalMonetaryCost = wonder.getCost().calculateTotalMonetaryCost(player.getHand(), game.findNonActivePlayer().getHand(), player.calculateWondersConstructed(), player.getTokens().contains(ProgressToken.ARCHITECTURE));
        if (totalMonetaryCost > player.getMoney()) {
            throw new InvalidMoveException("Player does not have enough money to make purchase");
        }
        player.setMoney(player.getMoney() - totalMonetaryCost);
        if (game.findNonActivePlayer().getTokens().contains(ProgressToken.ECONOMY)) {
            game.findNonActivePlayer().setMoney(game.findNonActivePlayer().getMoney() + totalMonetaryCost);
        }
        player.purchaseWonder(wonder, game.getAge());
        player.setMoney(player.getMoney() + wonder.getMonetaryGain());
        if (wonder.getMilitaryGain() > 0) {
            if (game.getCurrentPlayerNumber() == 1) {
                game.getMilitary().setMilitaryPosition(game.getMilitary().getMilitaryPosition()+ wonder.getMilitaryGain());
            } else {
                game.getMilitary().setMilitaryPosition(game.getMilitary().getMilitaryPosition() - wonder.getMilitaryGain());
            }
            game.applyMilitaryEffect();
        }
        switch (wonder) {
            case THE_APPIAN_WAY:
                game.findNonActivePlayer().decreaseMoneyButNotIntoNegative(3);
                break;
            case CIRCUS_MAXIMUS:
                if (game.findNonActivePlayer().getHand().stream().filter(cardName -> cardName.getCard().getCardType() == CardOrValueType.MANUFACTURED_GOOD).count() > 0) {
                    game.setStep(GameStep.DESTROY_GREY);
                }
                break;
            case THE_GREAT_LIBRARY:
                List<ProgressToken> allUnavailableTokens = new ArrayList<>(game.getTokensUnavailable());
                Collections.shuffle(allUnavailableTokens);
                game.getTokensFromUnavailable().clear();
                game.getTokensFromUnavailable().addAll(allUnavailableTokens.subList(0,3));
                game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN_FROM_DISCARD);
                break;
            case THE_MAUSOLEUM:
                if (!game.getDiscardedCards().isEmpty()) {
                    game.setStep(GameStep.CONSTRUCT_FROM_DISCARD);
                }
                break;
            case THE_STATUE_OF_ZEUS:
                if (game.findNonActivePlayer().getHand().stream().filter(cardName -> cardName.getCard().getCardType() == CardOrValueType.RAW_MATERIAL).count() > 0) {
                    game.setStep(GameStep.DESTROY_BROWN);
                }
                break;
            default:
                break;

        }
        if (game.getStep() == GameStep.PLAY_CARD) {
            Boolean immediatelyPlaySecondTurn = wonder.immediatelyPlaySecondTurn() || player.getTokens().contains(ProgressToken.THEOLOGY);
            endTurn(game, immediatelyPlaySecondTurn);
        }
        game.getPyramid().remove(cardIndex);
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
        Integer totalMonetaryCost = cardName.getCard().getCost().calculateTotalMonetaryCost(player.getHand(), game.findNonActivePlayer().getHand(), player.calculateWondersConstructed(), buildWithTwoFewer);
        if (totalMonetaryCost > player.getMoney()) {
            throw new InvalidMoveException("Player does not have enough money to make purchase");
        }
        if (totalMonetaryCost != 0 && opponent.getTokens().contains(ProgressToken.ECONOMY)) {
            Integer totalTradeCost = totalMonetaryCost - cardName.getCard().getCost().getMonetaryCost();
            opponent.setMoney(opponent.getMoney() + totalTradeCost);
        }
        player.setMoney(player.getMoney() - totalMonetaryCost);
        if (player.getTokens().contains(ProgressToken.URBANISM) && cardName.getCard().getCost().constructForFree(player.getHand())) {
            player.setMoney(player.getMoney() + 4);
        }
        buildingEffects(game, cardName);
        game.getPyramid().remove(cardIndex);
        if (game.getStep() == GameStep.PLAY_CARD) {
            endTurn(game);
        }
        save(game);
        return game;
    }

    @Transactional
    public Game constructBuildingFromDiscard(String code, CardName cardName) throws InvalidMoveException, GameCodeNotFoundException {
        Game game = findByCode(code);
        confirmCorrectStep(game, GameStep.CONSTRUCT_FROM_DISCARD);
        if (!game.getDiscardedCards().contains(cardName)) {
            throw new InvalidMoveException("Card not found in discard.");
        }
        Boolean immediateSecondTurn = game.findActivePlayer().getTokens().contains(ProgressToken.THEOLOGY);
        buildingEffects(game, cardName);
        game.getDiscardedCards().remove(cardName);
        if (game.getStep() == GameStep.CONSTRUCT_FROM_DISCARD) {
            endTurn(game, immediateSecondTurn);
        }
        save(game);
        return game;
    }

    @Transactional
    public Game chooseProgressToken(String code, ProgressToken progressToken) throws GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        confirmCorrectStep(game, GameStep.CHOOSE_PROGRESS_TOKEN);
        if (!game.getTokensAvailable().contains(progressToken)) {
            throw new InvalidMoveException("Token not available to be chosen");
        }
        progressEffects(game, progressToken);
        game.getTokensAvailable().remove(progressToken);
        if (game.getStep() == GameStep.CHOOSE_PROGRESS_TOKEN) {
            endTurn(game);
        }
        save(game);
        return game;
    }

    @Transactional
    public Game chooseProgressTokenFromDiscard(String code, ProgressToken progressToken) throws GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        confirmCorrectStep(game, GameStep.CHOOSE_PROGRESS_TOKEN_FROM_DISCARD);
        if (!game.getTokensFromUnavailable().contains(progressToken)) {
            throw new InvalidMoveException("Token not available to be chosen.");
        }
        Boolean immediateSecondTurn = game.findActivePlayer().getTokens().contains(ProgressToken.THEOLOGY);
        progressEffects(game, progressToken);
        game.getTokensFromUnavailable().clear();
        if (game.getStep() == GameStep.CHOOSE_PROGRESS_TOKEN_FROM_DISCARD) {
            endTurn(game, immediateSecondTurn);
        }
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

    @Transactional
    public Game destroyCard(String code, CardName card) throws GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        if (game.getStep() != GameStep.DESTROY_BROWN && game.getStep() != GameStep.DESTROY_GREY) {
            throw new InvalidMoveException("Incorrect game step for this move.");
        }
        if (card.getCard().getCardType() == CardOrValueType.RAW_MATERIAL) {
            confirmCorrectStep(game, GameStep.DESTROY_BROWN);
        } else if (card.getCard().getCardType() == CardOrValueType.MANUFACTURED_GOOD) {
            confirmCorrectStep(game, GameStep.DESTROY_GREY);
        } else {
            throw new InvalidMoveException("Card is not the correct type for this game step.");
        }
        Player opponent = game.findNonActivePlayer();
        if (!opponent.getHand().contains(card)) {
            throw new InvalidMoveException("Opponent does not have this card in their hand.");
        }
        opponent.getHand().remove(card);
        game.getDiscardedCards().add(card);
        Boolean immediateSecondTurn = game.findActivePlayer().getTokens().contains(ProgressToken.THEOLOGY);
        endTurn(game, immediateSecondTurn);
        save(game);
        return game;
    }

    //TODO:removeme
    @Transactional
    public Game testStuff(String code) throws  GameCodeNotFoundException, InvalidMoveException {
        Game game = findByCode(code);
        calculateScores(game);
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
        endTurn(game, false);
    }

    private static void endTurn(Game game, Boolean immediatelyPlaySecondTurn) throws InvalidMoveException {
        if (!immediatelyPlaySecondTurn) {
            game.changeCurrentPlayer();
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
                    calculateScores(game);
                    break;
                default:
                    throw new InvalidMoveException("Age is not valid");
            }
        }
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

    private static void buildingEffects(Game game, CardName cardName) throws InvalidMoveException {
        Player player = game.findActivePlayer();
        Integer monetaryGain = 0;
        switch (cardName.getCard().getCardType()) {
            case COMMERCIAL_BUILDING:
                CommercialBuildingCard commercialBuildingCard = (CommercialBuildingCard) cardName.getCard();
                if (commercialBuildingCard.getMoney() > 0) {
                    if (commercialBuildingCard.getMoneyPerType() != null) {
                        if (commercialBuildingCard.getMoneyPerType() == CardOrValueType.WONDER) {
                            monetaryGain = player.calculateWondersConstructed().size() * commercialBuildingCard.getMoney();
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
                        monetaryGain = guildCard.getMoneyPerValueType(); //0
                        break;
                    case WONDER:
                        monetaryGain = guildCard.getMoneyPerValueType() * player.calculateWondersConstructed().size();
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
                if (player.checkNewScienceMatch(scientificBuildingCard.getScienceSymbol()) && !game.getTokensAvailable().isEmpty()) {
                    game.setStep(GameStep.CHOOSE_PROGRESS_TOKEN);
                }
                break;
            default:
                break;
        }
        player.setMoney(player.getMoney() + monetaryGain);
        player.getHand().add(cardName);
    }


    private static void confirmCorrectStep(Game game, GameStep expectedStep) throws InvalidMoveException {
        if (game.getStep() != expectedStep) {
            throw new InvalidMoveException("Incorrect game step for this move");
        }
    }

    private static void calculateScores(Game game) throws InvalidMoveException {
        confirmCorrectStep(game, GameStep.GAME_END);
        Map<CardOrValueType, Integer> player1Score = new EnumMap<CardOrValueType, Integer>(CardOrValueType.class);
        player1Score.put(CardOrValueType.CIVILIAN_BUILDING, ScoreUtility.calculateSimpleCategoryScore(game.getPlayer1().getHand(), CardOrValueType.CIVILIAN_BUILDING));
        player1Score.put(CardOrValueType.SCIENTIFIC_BUILDING, ScoreUtility.calculateSimpleCategoryScore(game.getPlayer1().getHand(), CardOrValueType.SCIENTIFIC_BUILDING));
        player1Score.put(CardOrValueType.COMMERCIAL_BUILDING, ScoreUtility.calculateSimpleCategoryScore(game.getPlayer1().getHand(), CardOrValueType.COMMERCIAL_BUILDING));
        player1Score.put(CardOrValueType.GUILD, ScoreUtility.calculateGuildScore(game.getPlayer1(), game.getPlayer2()));
        player1Score.put(CardOrValueType.WONDER, ScoreUtility.calculateWonderScore(game.getPlayer1().calculateWondersConstructed()));
        player1Score.put(CardOrValueType.PROGRESS_TOKEN, ScoreUtility.calculateProgressTokenScore(game.getPlayer1().getTokens()));
        player1Score.put(CardOrValueType.MONEY, ScoreUtility.calculateMoneyScore(game.getPlayer1().getMoney()));
        player1Score.put(CardOrValueType.MILITARY_BUILDING, ScoreUtility.calculatePlayer1MilitaryScore(game.getMilitary().getMilitaryPosition()));
        game.getPlayer1().setScore(player1Score);
        Integer player1Total = ScoreUtility.calculateTotal(player1Score);

        Map<CardOrValueType, Integer> player2Score = new EnumMap<CardOrValueType, Integer>(CardOrValueType.class);
        player2Score.put(CardOrValueType.CIVILIAN_BUILDING, ScoreUtility.calculateSimpleCategoryScore(game.getPlayer2().getHand(), CardOrValueType.CIVILIAN_BUILDING));
        player2Score.put(CardOrValueType.SCIENTIFIC_BUILDING, ScoreUtility.calculateSimpleCategoryScore(game.getPlayer2().getHand(), CardOrValueType.SCIENTIFIC_BUILDING));
        player2Score.put(CardOrValueType.COMMERCIAL_BUILDING, ScoreUtility.calculateSimpleCategoryScore(game.getPlayer2().getHand(), CardOrValueType.COMMERCIAL_BUILDING));
        player2Score.put(CardOrValueType.GUILD, ScoreUtility.calculateGuildScore(game.getPlayer2(), game.getPlayer1()));
        player2Score.put(CardOrValueType.WONDER, ScoreUtility.calculateWonderScore(game.getPlayer2().calculateWondersConstructed()));
        player2Score.put(CardOrValueType.PROGRESS_TOKEN, ScoreUtility.calculateProgressTokenScore(game.getPlayer2().getTokens()));
        player2Score.put(CardOrValueType.MONEY, ScoreUtility.calculateMoneyScore(game.getPlayer2().getMoney()));
        player2Score.put(CardOrValueType.MILITARY_BUILDING, ScoreUtility.calculatePlayer2MilitaryScore(game.getMilitary().getMilitaryPosition()));
        game.getPlayer2().setScore(player2Score);
        Integer player2Total = ScoreUtility.calculateTotal(player2Score);

        if(player1Total > player2Total) {
            game.getPlayer1().setWon(true);
        } else if (player2Total > player1Total) {
            game.getPlayer2().setWon(true);
        } else if (player1Score.get(CardOrValueType.CIVILIAN_BUILDING) > player2Score.get(CardOrValueType.CIVILIAN_BUILDING)) {
            game.getPlayer1().setWon(true);
        } else if (player2Score.get(CardOrValueType.CIVILIAN_BUILDING) > player1Score.get(CardOrValueType.CIVILIAN_BUILDING)) {
            game.getPlayer2().setWon(true);
        } else {
            game.getPlayer1().setWon(true);
            game.getPlayer2().setWon(true);
        }
    }

}
