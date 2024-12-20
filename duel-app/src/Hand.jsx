import Card from './Card';

export default function Hand({num, cards, money}) {

    return (<div className={num+"playerHand"}>
        <h4>Player {num}</h4>
        <h5>Money: {money}</h5>
        {cards.map((card) =>
            <Card 
            key={card}
            cardName={card} 
            isActive={false} 
            cardSetter={null}>
            </Card>) }
    </div>)

}