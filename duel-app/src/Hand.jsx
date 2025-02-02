import Card from './Card';
import Tokens from './Tokens';
import Wonder from './Wonder';
import Coin from './Coin';

export default function Hand({money, wonders, tokens, sortedHand, onClickWonder, destroyCard}) {

  return (
    <div className="playerHand">
        <Coin money={money}></Coin>
      <Tokens tokens={tokens}></Tokens>
      <div className="wonders">
        {Object.entries(wonders).map(([wonderName, age]) => (
        <Wonder 
            key={wonderName} 
            wonderName={wonderName} 
            onClickWonder={() => onClickWonder(wonderName)} 
            wonderBackAge={age}
        />
        ))}
      </div>
      <div className="playerCards">
      {Object.values(sortedHand).map((items, index) => (
          <div className="cardColumn" key={index}>
            {items.map((item) => (
              <Card 
                key={item} 
                cName="playerCard" 
                cardName={item}
                otherOnClick={destroyCard} 
                />
            ))}
          </div>
        ))}
      </div>
    </div>
  );
}