import {tokenImgMap} from './tokenMap.js';

export default function Tokens({tokens, onClickToken}) {
    return (
        <>
        {Array.from(tokens).map((value) => (
            <img key={value} src={tokenImgMap[value]} onClick={() => onClickToken(value)}/>
          ))}
          </>
    )
}