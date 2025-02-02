import Card from "./Card"

export default function DiscardPile({cards, constructFromDiscard}) {
    return (
        <div className="discardPile">
            {cards.map((cardName) =>
                <Card cardName={cardName} otherOnClick={constructFromDiscard}></Card>
            )}
        </div>
    )
}