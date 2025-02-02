import { wonderImgMap } from "./wonderMap";
import ageOneBackSide from './img/ageOneBackSide.jpg';
import ageTwoBackSide from './img/ageTwoBackSide.jpg';
import ageThreeBackSide from './img/ageThreeBackSide.jpg';

export default function Wonder({wonderName, onClickWonder, wonderBackAge}) {

    const cardBackMap = {
        1: ageOneBackSide,
        2: ageTwoBackSide,
        3: ageThreeBackSide
    }
    return (<div className="wonder">
        <img className="wonderCard" src={wonderImgMap[wonderName]} onClick={onClickWonder}/>
        {wonderBackAge != 0 &&
        <img class="wonderBack" src={cardBackMap[wonderBackAge]}></img>}
    </div>);
}