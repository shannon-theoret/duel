export default function Instructions({step, currentPlayerNumber, cardSelected}) {

    return (
        <div>
            {step !== "SETUP" && (
                <h5>Player {currentPlayerNumber}'s Turn</h5>
            )}
            {step === "WONDER_SELECTION" && (
                <p>Select one of the wonders below to add to your hand.</p>
            )}
            {step === "PLAY_CARD" && cardSelected === null && (
                <p>Select a card from the pyramid</p>
            )}
            {step === "PLAY_CARD" && cardSelected !== null && (
                <p>Choose an action for this card or select one of your wonders to construct</p>
            )}
            {step == "CHOOSE_PROGRESS_TOKEN" && (
                <p>Choose one of the available Progress Tokens</p>
            )}
            {step == "DESTROY_GREY" && (
                <p>Choose one of the grey cards constructed by your opponent to add to the discard.</p>
            )}
            {step == "DESTROY_BROWN" && (
                <p>Choose one of the brown cards constructed by your opponent to add to the discard.</p>
            )}
            {step === "CONSTRUCT_FROM_DISCARD" && (
                <p>Choose one of the discarded cards to construct for free.</p>
            )}
            {step === "CHOOSE_PROGRESS_TOKEN_FROM_DISCARD" && (
                <p>Choose one of the 3 Progress tokens from among those discarded at the beginning of the game to play it.</p>
            )}
        </div>
    )
}