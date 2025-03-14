import { wonderImgMap } from "./wonderMap";
import ageOneBackSide from './img/ageOneBackSide.jpg';
import ageTwoBackSide from './img/ageTwoBackSide.jpg';
import ageThreeBackSide from './img/ageThreeBackSide.jpg';
import Tooltip from "./Tooltip";

export default function Wonder({wonderName, onClickWonder, wonderBackAge}) {

    const cardBackMap = {
        1: ageOneBackSide,
        2: ageTwoBackSide,
        3: ageThreeBackSide
    }
    return (
    <Tooltip textKey={wonderName}>
    <div className="wonder">
        <img className="wonder-card" src={wonderImgMap[wonderName]} onClick={onClickWonder}/>
        {wonderBackAge != 0 &&
        <img className="wonder-back" src={cardBackMap[wonderBackAge]}></img>}
    </div>
    </Tooltip>);
}