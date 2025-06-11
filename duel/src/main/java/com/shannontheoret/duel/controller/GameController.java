package com.shannontheoret.duel.controller;

import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.Wonder;
import com.shannontheoret.duel.card.CardName;
import com.shannontheoret.duel.entity.Game;
import com.shannontheoret.duel.exceptions.GameCodeNotFoundException;
import com.shannontheoret.duel.exceptions.InvalidMoveException;
import com.shannontheoret.duel.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = {"https://duel.shannontheoret.com", "http://localhost:5173"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowCredentials = "true"
)
@RestController
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("{gameCode}")
    public ResponseEntity<Object> getGame(@PathVariable("gameCode") String gameCode) throws GameCodeNotFoundException {
        try {
            return ResponseEntity.ok(gameService.findByCode(gameCode));
        } catch (GameCodeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("newGame")
    public ResponseEntity<Object> newGame() {
       return ResponseEntity.ok(gameService.newGame());
    }

    @PostMapping("{gameCode}/selectWonder")
    public ResponseEntity<Object> selectWonder(@PathVariable("gameCode") String gameCode, @RequestParam Wonder wonder) throws GameCodeNotFoundException, InvalidMoveException {
        try {
            return ResponseEntity.ok(gameService.selectWonder(gameCode, wonder));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("{gameCode}/constructWonder")
    public ResponseEntity<Object> constructWonder(@PathVariable("gameCode") String gameCode, @RequestParam Integer index, @RequestParam Wonder wonder) throws GameCodeNotFoundException, InvalidMoveException {
        try {
            return ResponseEntity.ok(gameService.constructWonder(gameCode, index, wonder));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }     }

    @PostMapping("{gameCode}/constructBuilding")
    public ResponseEntity<Object> constructBuilding(@PathVariable("gameCode") String gameCode, @RequestParam Integer index) throws GameCodeNotFoundException, InvalidMoveException {
        try {
            return ResponseEntity.ok(gameService.constructBuilding(gameCode, index));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("{gameCode}/constructBuildingFromDiscard")
    public ResponseEntity<Object> constructBuildingFromDiscard(@PathVariable("gameCode") String gameCode, @RequestParam CardName cardName) throws GameCodeNotFoundException, InvalidMoveException {
        try {
            return ResponseEntity.ok(gameService.constructBuildingFromDiscard(gameCode, cardName));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("{gameCode}/discard")
    public ResponseEntity<Object> discard(@PathVariable("gameCode") String gameCode, @RequestParam Integer index) throws GameCodeNotFoundException, InvalidMoveException {
        try {
            return ResponseEntity.ok(gameService.discard(gameCode, index));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("{gameCode}/chooseProgressToken")
    public ResponseEntity<Object> chooseProgressToken(@PathVariable("gameCode") String gameCode, @RequestParam ProgressToken progressToken) throws GameCodeNotFoundException, InvalidMoveException {
        try {
            return ResponseEntity.ok(gameService.chooseProgressToken(gameCode, progressToken));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("{gameCode}/chooseProgressTokenFromDiscard")
    public ResponseEntity<Object> chooseProgressTokenFromDiscard(@PathVariable("gameCode") String gameCode, @RequestParam ProgressToken progressToken) throws GameCodeNotFoundException, InvalidMoveException {
        try {
            return ResponseEntity.ok(gameService.chooseProgressTokenFromDiscard(gameCode, progressToken));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("{gameCode}/destroyCard")
    public ResponseEntity<Object> destroyCard(@PathVariable("gameCode") String gameCode, @RequestParam CardName cardName) {
        try {
            return ResponseEntity.ok(gameService.destroyCard(gameCode, cardName));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("{gameCode}/makeAIMove")
    public ResponseEntity<Object> makeAIMove(@PathVariable("gameCode") String gameCode) {
        try {
            return ResponseEntity.ok(gameService.makeAIMove(gameCode));
        } catch (GameCodeNotFoundException | InvalidMoveException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/ping") //avoids cold start latency
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }


}
