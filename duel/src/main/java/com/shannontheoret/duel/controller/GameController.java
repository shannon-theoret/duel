package com.shannontheoret.duel.controller;

import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.entity.Game;
import com.shannontheoret.duel.exceptions.GameCodeNotFoundException;
import com.shannontheoret.duel.exceptions.InvalidMoveException;
import com.shannontheoret.duel.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("{gameCode}")
    public Game getGame(@PathVariable("gameCode") String gameCode) throws GameCodeNotFoundException {
        return gameService.findByCode(gameCode);
    }

    @PostMapping("newGame")
    public Game newGame() {
        return gameService.newGame();
    }

    @PostMapping("{gameCode}/constructBuilding")
    public Game constructBuilding(@PathVariable("gameCode") String gameCode, @RequestParam Integer index) throws GameCodeNotFoundException, InvalidMoveException {
        return gameService.constructBuilding(gameCode, index);
    }

    @PostMapping("{gameCode}/discard")
    public Game discard(@PathVariable("gameCode") String gameCode, @RequestParam Integer index) throws GameCodeNotFoundException, InvalidMoveException {
        return gameService.discard(gameCode, index);
    }

    @PostMapping("{gameCode}/chooseProgressToken")
    public Game chooseProgressToken(@PathVariable("gameCode") String gameCode, @RequestParam ProgressToken progressToken) throws GameCodeNotFoundException, InvalidMoveException {
        return gameService.chooseProgressToken(gameCode, progressToken);
    }

    @PostMapping("{gameCode}/testStuff")
    public Game testStuff(@PathVariable("gameCode") String gameCode) throws GameCodeNotFoundException, InvalidMoveException {
        return gameService.testStuff(gameCode);
    }


}
