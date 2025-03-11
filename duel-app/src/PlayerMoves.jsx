import Instructions from "./Instructions";
import WonderSelection from "./WonderSelection";
import Button from "./Button";

export default function PlayerMoves({ game, selectedCardIndex, handleConstructBuilding, handleDiscard, handleSelectWonder, handleConstructWonder, handleDestroyCard }) {
    return (
        <div className="playerMoves">
            <Instructions step={game.step} currentPlayerNumber={game.currentPlayerNumber} cardSelected={selectedCardIndex}></Instructions>
            {game.step === "WONDER_SELECTION" ?
                (<WonderSelection wonders={game.wondersAvailable} selectWonder={handleSelectWonder}></WonderSelection>) : null}
            {selectedCardIndex !== null && game.step === "PLAY_CARD" ?
                (<div>
                    <Button text="Construct the Building" onClick={handleConstructBuilding}></Button>
                    <Button text="Discard the card to obtain coins" onClick={handleDiscard}></Button>
                </div>) : null}
        </div>
    );
}