import './Board.css';
import Card from './Card';

export default function Board({cardSetter, age, cards}) {

    const pyramid = {
        1: {
            "row1": [
                {
                    index: 0,
                    column: "c5"
                },
                {
                    index: 1,
                    column: "c7"
                }
            ],
            "row2": [
                {
                    index: 2,
                    column: "c4"
                },
                {
                    index: 3,
                    column: "c6"
                },
                {
                    index: 4,
                    column: "c8"
                }
            ],
            "row3": [
                {
                    index: 5,
                    column: "c3"
                },
                {
                    index: 6,
                    column: "c5"
                },
                {
                    index: 7,
                    column: "c7"
                },
                {
                    index: 8,
                    column: "c9"
                }
            ],
            "row4": [
                {
                    index: 9,
                    column: "c2"
                },
                {
                    index: 10,
                    column: "c4"
                },
                {
                    index: 11,
                    column: "c6"
                },
                {
                    index: 12,
                    column: "c8"
                },
                {
                    index: 13,
                    column: "c10"
                }
            ],
            "row5": [
                {
                    index: 14,
                    column: "c1"
                },
                {
                    index: 15,
                    column: "c3"
                },
                {
                    index: 16,
                    column: "c5"
                },
                {
                    index: 17,
                    column: "c7"
                },
                {
                    index: 18,
                    column: "c9"
                },
                {
                    index: 19,
                    column: "c11"
                }
            ]
        },
        2 : {
            "row1": [
                {
                    index: 0,
                    column: "c1"
                },
                {
                    index: 1,
                    column: "c3"
                },
                {
                    index: 2,
                    column: "c5"
                },
                {
                    index: 3,
                    column: "c7"
                },
                {
                    index: 4,
                    column: "c9"
                },
                {
                    index: 5,
                    column: "c11"
                }
            ],
            "row2": [
                {
                    index: 6,
                    column: "c2"
                },
                {
                    index: 7,
                    column: "c4"
                },
                {
                    index: 8,
                    column: "c6"
                },
                {
                    index: 9,
                    column: "c8"
                },
                {
                    index: 10,
                    column: "c10"
                }
            ],
            "row3": [
                {
                    index: 11,
                    column: "c3"
                },
                {
                    index: 12,
                    column: "c5"
                },
                {
                    index: 13,
                    column: "c7"
                },
                {
                    index: 14,
                    column: "c9"
                }
            ],
            "row4": [
                {
                    index: 15,
                    column: "c4"
                },
                {
                    index: 16,
                    column: "c6"
                },
                {
                    index: 17,
                    column: "c8"
                }
            ],
            "row5": [
                {
                    index: 18,
                    column: "c5"
                },
                {
                    index: 19,
                    column: "c7"
                }
            ]
        },
        3: {
            "row1": [
                {
                    index: 0,
                    column: "c5"
                },
                {
                    index: 1,
                    column: "c7"
                }
            ],
            "row2": [
                {
                    index: 2,
                    column: "c4"
                },
                {
                    index: 3,
                    column: "c6"
                },
                {
                    index: 4,
                    column: "c8"
                }
            ],
            "row3": [
                {
                    index: 5,
                    column: "c3"
                },
                {
                    index: 6,
                    column: "c5"
                },
                {
                    index: 7,
                    column: "c7"
                },
                {
                    index: 8,
                    column: "c9"
                }
            ],
            "row4": [
                {
                    index: 9,
                    column: "c4"
                },
                {
                    index: 10,
                    column: "c8"
                }
            ],
            "row5": [
                {
                    index: 11,
                    column: "c3"
                },
                {
                    index: 12,
                    column: "c5"
                },
                {
                    index: 13,
                    column: "c7"
                },
                {
                    index: 14,
                    column: "c9"
                }
            ],
            "row6": [
                {
                    index: 15,
                    column: "c4"
                },
                {
                    index: 16,
                    column: "c6"
                },
                {
                    index: 17,
                    column: "c8"
                }
            ],
            "row7": [
                {
                    index: 18,
                    column: "c5"
                },
                {
                    index: 19,
                    column: "c7"
                }
            ],
        }        
    }

    function createRow(row) {
        return pyramid[age][row].map(rowItem => 
            rowItem["index"] in cards?
            <Card 
                key={rowItem["index"]}
                cName={rowItem["column"]} 
                index={rowItem["index"]}
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