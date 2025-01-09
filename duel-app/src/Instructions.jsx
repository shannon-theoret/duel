export default function Instructions({step, currentPlayerNumber, cardSelected}) {

    console.log("cardsel: " + cardSelected);
    return (
        <div>
            {step !== "SETUP" && (
                <h5>Player {currentPlayerNumber}'s Turn</h5>
            )}
            {step === "PLAY_CARD" && cardSelected == null && (
                <p>Select a card from the pyramid</p>
            )}
            {step === "PLAY_CARD" && cardSelected != null && (
                <p>Choose an action for this card or select one of your wonders to construct</p>
            )}
            {step == "CHOOSE_SCIENCE" && (
                <p>Choose one of the available Progress Tokens</p>
            )}
        </div>
    )
}