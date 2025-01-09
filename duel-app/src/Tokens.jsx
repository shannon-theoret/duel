import {tokenImgMap} from './tokenMap.js';

export default function Tokens({tokens}) {
    return (
        <div>
        {Array.from(tokens).map((value) => (
            <img key={value} src={tokenImgMap[value]}/>
          ))}
          </div>
    )
}