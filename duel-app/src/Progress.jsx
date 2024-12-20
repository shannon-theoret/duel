import progressBar from './img/progressBar.png';
import militaryToken from './img/militaryToken.png';
import agriculture from './img/agriculture.png';
import architecture from './img/architecture.png';
import economy from './img/economy.png';
import law from './img/law.png';
import masonry from './img/masonry.png';
import philosophy from './img/philosophy.png';
import strategy from './img/strategy.png';
import theology from './img/theology.png';
import urbanism from './img/urbanism.png';

export default function Progress({military, tokensAvailable}) {

    const tokenImgMap = {
        "AGRICULTURE": agriculture,
        "ARCHITECTURE": architecture,
        "ECONOMY": economy,
        "LAW": law,
        "MASONRY": masonry,
        "PHILOSOPHY": philosophy,
        "STRATEGY": strategy,
        "THEOLOGY": theology,
        "URBANISM": urbanism
      };

      console.log(tokensAvailable);
      const tokenImgs = Array.from(tokensAvailable).map((value, index) => (
        <img className={`progress${index}`} src={tokenImgMap[value]}/>
      ));

    return (
    <div className="progress">
        <img src={progressBar}></img>
        <img className={`military${military.militaryPosition}`} src={militaryToken}></img>
        {tokenImgs}
    </div>
    )
}