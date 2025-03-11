import progressBar from './img/progressBar.png';
import militaryToken from './img/militaryToken.png';
import {tokenImgMap} from './tokenMap.js';
import broken2left from './img/broken2left.png';
import broken2right from './img/broken2right.png';
import broken5left from './img/broken5left.png';
import broken5right from './img/broken5right.png';

export default function Progress({military, tokensAvailable, onTokenClick, chooseScience}) {

      const tokenImgs = Array.from(tokensAvailable).map((value, index) => (
      <img className={`progress${index}`} src={tokenImgMap[value]} onClick={chooseScience ? () => onTokenClick(value) : null}/>
      ));

    return (
    <div className="progress">
        <img src={progressBar}></img>
        <img className={`military${military.militaryPosition}`} src={militaryToken}></img>
        {tokenImgs}
        {military.loot5Player1Available && <img className='loot5-player1-available' src={broken5left}></img>}
        {military.loot5Player2Available && <img className='loot5-player2-available' src={broken5right}></img>}
        {military.loot2Player1Available && <img className='loot2-player1-available' src={broken2left}></img>}
        {military.loot2Player2Available && <img className='loot2-player2-available' src={broken2right}></img>}
    </div>
    )
}