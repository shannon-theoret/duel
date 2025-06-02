import './Instructions.css';

export default function Instructions({step, currentPlayerNumber, cardSelected}) {

    return (
        <div className='instructions-container'>
            {step !== "SETUP" && step !== "GAME_END" && (
                <strong>PLAYER {currentPlayerNumber}'S TURN</strong>
            )}
            {step === "WONDER_SELECTION" && (
                <span>Select one of the wonders below to add to your hand.</span>
            )}
            {step === "PLAY_CARD" && cardSelected === null && (
                <span>Select a card from the pyramid</span>
            )}
            {step === "PLAY_CARD" && cardSelected !== null && (
                <span>Choose an action for this card or select one of your wonders to construct</span>
            )}
            {step == "CHOOSE_PROGRESS_TOKEN" && (
                <span>Choose one of the available Progress Tokens</span>
            )}
            {step == "DESTROY_GREY" && (
                <span>Choose one of the grey cards constructed by your opponent to add to the discard.</span>
            )}
            {step == "DESTROY_BROWN" && (
                <span>Choose one of the brown cards constructed by your opponent to add to the discard.</span>
            )}
            {step === "CONSTRUCT_FROM_DISCARD" && (
                <span>Choose one of the discarded cards to construct for free.</span>
            )}
            {step === "CHOOSE_PROGRESS_TOKEN_FROM_DISCARD" && (
                <span>Choose one of the 3 Progress tokens from among those discarded at the beginning of the game to play it.</span>
            )}
        </div>
    )
}