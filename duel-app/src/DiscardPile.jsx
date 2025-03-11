import Card from "./Card"

export default function DiscardPile({cards, constructFromDiscard}) {
    return (
        <div className="discard-pile">
            {cards.map((cardName) =>
                <Card key={cardName} cardName={cardName} otherOnClick={constructFromDiscard}></Card>
            )}
        </div>
    )
}