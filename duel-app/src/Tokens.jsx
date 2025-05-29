import './Tokens.css';
import {tokenImgMap} from './tokenMap.js';
import Tooltip from './Tooltip.jsx';

export default function Tokens({tokens, onClickToken}) {
    return (
        <div className='tokens'>
        {Array.from(tokens).map((value) => (
            <Tooltip textKey={value}><img className="token" key={value} src={tokenImgMap[value]} onClick={() => onClickToken(value)}/></Tooltip>
          ))}
          </div>
    )
}