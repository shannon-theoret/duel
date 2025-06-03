import './PlayerMoves.css';
import Instructions from "./Instructions";
import WonderSelection from "./WonderSelection";
import Button from "./Button";

export default function PlayerMoves({ game, selectedCardIndex, handleConstructBuilding, handleDiscard, handleSelectWonder, handleMakeAiMove }) {
    return (
        <div className="player-moves">
            <Instructions step={game.step} currentPlayerNumber={game.currentPlayerNumber} cardSelected={selectedCardIndex}></Instructions>
            {game.step === "WONDER_SELECTION" ?
                (<WonderSelection wonders={game.wondersAvailable} selectWonder={handleSelectWonder}></WonderSelection>) : null}
            {selectedCardIndex !== null && game.step === "PLAY_CARD" ?
                (<div>
                    <Button text="Construct the Building" onClick={handleConstructBuilding}></Button>
                    <Button text="Discard the card to obtain coins" onClick={handleDiscard}></Button>
                </div>) : null}
            {game.currentPlayerNumber === 2?
                (<div><Button text="Have AI Player Make Move" onClick={handleMakeAiMove}></Button></div>): null}      
        </div>
    );
}