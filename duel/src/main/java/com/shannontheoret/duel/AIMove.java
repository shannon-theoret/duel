package com.shannontheoret.duel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shannontheoret.duel.PlayerMove;
import com.shannontheoret.duel.ProgressToken;
import com.shannontheoret.duel.Wonder;
import com.shannontheoret.duel.card.CardName;

public class AIMove {

    @JsonProperty(required = true)
    private PlayerMove move;

    @JsonProperty(required = true)
    private MoveData moveData;

    public PlayerMove getMove() {
        return move;
    }

    public void setMove(PlayerMove move) {
        this.move = move;
    }

    public MoveData getMoveData() {
        return moveData;
    }

    public void setMoveData(MoveData moveData) {
        this.moveData = moveData;
    }

    public static class MoveData {

        private Integer cardIndex;
        private Wonder wonder;
        private ProgressToken progressToken;
        private CardName cardName;

        public Integer getCardIndex() {
            return cardIndex;
        }

        public void setCardIndex(Integer cardIndex) {
            this.cardIndex = cardIndex;
        }

        public Wonder getWonder() {
            return wonder;
        }

        public void setWonder(Wonder wonder) {
            this.wonder = wonder;
        }

        public ProgressToken getProgressToken() {
            return progressToken;
        }

        public void setProgressToken(ProgressToken progressToken) {
            this.progressToken = progressToken;
        }

        public CardName getCardName() {
            return cardName;
        }

        public void setCardName(CardName cardName) {
            this.cardName = cardName;
        }
    }
}
