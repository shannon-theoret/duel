import Card from './Card';
import Tokens from './Tokens';

export default function Hand({ num, money, tokens, sortedHand }) {

  return (
    <div className="playerHand">
      <h4>Player {num}</h4>
      <h5>Money: {money}</h5>
      <Tokens tokens={tokens}></Tokens>
      <div className="playerCards">
      {Object.values(sortedHand).map((items, index) => (
          <div className="cardColumn" key={index}>
            {items.map((item) => (
              <Card key={item} cName="playerCard" cardName={item} />
            ))}
          </div>
        ))}
      </div>
    </div>
  );
}