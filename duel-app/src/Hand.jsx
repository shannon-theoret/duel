import './Hand.css';
import Card from './Card';
import Tokens from './Tokens';
import Wonder from './Wonder';
import Coin from './Coin';
import Tooltip from './Tooltip';

export default function Hand({money, wonders, tokens, sortedHand, onClickWonder, destroyCard}) {
  return (
    <div className="player-hand">
        <Coin money={money}></Coin>
      <Tokens tokens={tokens}></Tokens>
      <div className="wonders">
        {Object.entries(wonders).map(([wonderName, age]) => (
        <Wonder 
            key={wonderName} 
            wonderName={wonderName} 
            onClickWonder={onClickWonder ? () => onClickWonder(wonderName) : null} 
            wonderBackAge={age}
        />
        ))}
      </div>
      <div className="player-cards">
      {Object.values(sortedHand).map((items, index) => (
          <div className="card-column" key={index}>
            {items.map((item) => (
              <Card 
                key={item} 
                cName="player-card" 
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