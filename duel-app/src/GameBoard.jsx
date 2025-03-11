import Board from "./Board";
import Collapsible from "./Collapsible";
import DiscardPile from "./DiscardPile";
import Progress from "./Progress";
import Tokens from "./Tokens";

export default function GameBoard({ game, setSelectedCardIndex, selectedCardIndex, handleChooseProgressTokenFromDiscard, handleChooseProgressToken, handleConstructFromDiscard }) {
  return (
    <div className="game-inner1">
      {game.step === "CHOOSE_PROGRESS_TOKEN_FROM_DISCARD" &&
        <Tokens tokens={game.tokensFromUnavailable} onClickToken={handleChooseProgressTokenFromDiscard}></Tokens>}
      <Progress chooseScience={game.step === "CHOOSE_PROGRESS_TOKEN"} military={game.military} tokensAvailable={game.tokensAvailable} onTokenClick={handleChooseProgressToken}></Progress>
      <Collapsible label="See all discarded card" defaultOpen={game.step === "CONSTRUCT_FROM_DISCARD"}>
        <DiscardPile cards={game.discardedCards} constructFromDiscard={game.step == "CONSTRUCT_FROM_DISCARD" ? handleConstructFromDiscard : null}></DiscardPile>
      </Collapsible>
      <Board cardSetter={setSelectedCardIndex} age={game.age} cards={game.visiblePyramid} selectedCardIndex={selectedCardIndex}></Board>
    </div>
  );
}
