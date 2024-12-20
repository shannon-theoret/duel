import { useState, useEffect } from "react";
import Board from "./Board";
import Button from "./Button";
import './Game.css';
import Hand from "./Hand";
import Progress from "./Progress";
import axios from "axios";
import { useParams } from "react-router-dom";
import Instructions from "../Instructions";

export default function Game() {

    const [selectedCardIndex, setSelectedCardIndex] = useState("");

    const {code} = useParams();
    
    const [game, setGame] = useState({
        "code": code,
        "step": "SETUP"
    })

    useEffect(() => {
      axios.get(`/api/${code}`).then((response) => {
          setGame(response.data);
      });
    }, []);

    const constructBuilding = () => {
          axios.post(`/api/${code}/constructBuilding`, null, {
            params: {
              index: selectedCardIndex
          }
          }).then((response) => {
            setGame(response.data);
            setSelectedCardIndex("");
          }).catch((error) => {
            console.error('Error:', error);
          });
    }

      function discard() {
        axios.post(`/api/${code}/discard`, null, {
          params: {
            index: selectedCardIndex
        }
        }).then((response) => {
            setGame(response.data);
            setSelectedCardIndex("");
          }).catch((error) => {
            console.error('Error:', error);
          });
      }

      function constructWonder() {
        console.log(selectedCardIndex + " was used to construct a wonder");
        setSelectedCard("");
      }

      function testStuff() {
          axios.post(`/api/${code}/testStuff`).then((response) => {
            setGame(response.data);
          });
      }

    return (
        game.step !== "SETUP" && (
        <div className="game">  
          <div className="gameInner1">
            <Progress military={game.military} tokensAvailable={game.tokensAvailable}></Progress>
            <Board cardSetter={setSelectedCardIndex} age={game.age} cards={game.visiblePyramid}></Board>
          </div>
          <div className="gameInner2">
            <div className="playerMoves">
              <Instructions step={game.step} currentPlayerNumber={game.currentPlayerNumber} cardSelected={selectedCardIndex}></Instructions>
              {selectedCardIndex && game.step === "PLAY_CARD"?
              (<><Button text="Construct the Building" onClick={constructBuilding}></Button>
              <Button text="Discard the card to obtain coins" onClick={discard}></Button> 
              <Button text="Construct a Wonder" onClick={constructWonder}></Button></>) :null}
            </div>
            {/*<Hand num={1} cards={game.player1.hand} money={game.player1.money}></Hand>
            <Hand num={2} cards={game.player2.hand} money={game.player2.money}></Hand>*/}
          </div>
        </div>)
        );
}