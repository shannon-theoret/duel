import './Coin.css';
import coin from './img/coin.png';

export default function Coin({money}) {
    return (<div className="coin-container">
        <div className="coin-amount">
          <p className="coin-amount-text">{money}</p>
        </div>
        <img src={coin} className="coin" />
      </div>);
}