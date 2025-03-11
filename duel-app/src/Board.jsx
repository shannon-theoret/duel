import './Board.css';
import Card from './Card';
import { pyramid } from './pyramidStructure';

export default function Board({cardSetter, age, cards, selectedCardIndex}) {

    
    function createRow(row) {
        return pyramid[age][row].map(rowItem => 
            rowItem["index"] in cards?
            <Card 
                key={rowItem["index"]}
                cName={rowItem["column"]} 
                index={rowItem["index"]}
                selected={rowItem["index"] === selectedCardIndex}
                cardName={cards[rowItem["index"]]["cardName"]} 
                isActive={cards[rowItem["index"]]["isActive"]}  
                cardSetter={cardSetter}>
                </Card> : null)
    }
            
    return (<div className="board">
            <div className="row1">
                {createRow("row1")}
            </div>
            <div className="row2">
                {createRow("row2")}
            </div>
            <div className="row3">
                {createRow("row3")}
            </div>
            <div className="row4">
                {createRow("row4")}
            </div>
            <div className="row5">
                {createRow("row5")}
            </div>
            {age === 3 && (
            <>
                <div className="row6">
                {createRow("row6")}
                </div>
                <div className="row7">
                {createRow("row7")}
                </div>
            </>
            )}
        </div>);
}