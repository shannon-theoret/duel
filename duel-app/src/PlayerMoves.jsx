import './PlayerMoves.css';
import Instructions from "./Instructions";
import WonderSelection from "./WonderSelection";
import Button from "./Button";

export default function PlayerMoves({ game, selectedCardIndex, handleConstructBuilding, handleDiscard, handleSelectWonder, handleMakeAiMove }) {

    const opponentsTurn = (game.currentPlayerNumber==2);
    return (
        <div className="player-moves">
            <p className='previousMove'>{game.previousMove}</p>
            <Instructions step={game.step} currentPlayerNumber={game.currentPlayerNumber} cardSelected={selectedCardIndex} opponentsTurn={opponentsTurn}></Instructions>
            {game.step === "WONDER_SELECTION" ?
                (<WonderSelection wonders={game.wondersAvailable} selectWonder={handleSelectWonder}></WonderSelection>) : null}
            {selectedCardIndex !== null && game.step === "PLAY_CARD" && !opponentsTurn?
                (<div>
                    <Button text="Construct the Building" onClick={handleConstructBuilding}></Button>
                    <Button text="Discard the card to obtain coins" onClick={handleDiscard}></Button>
                </div>) : null}
        </div>
    );
}