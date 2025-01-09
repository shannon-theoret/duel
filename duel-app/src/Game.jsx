import { useState, useEffect } from "react";
import Board from "./Board";
import Button from "./Button";
import './Game.css';
import Hand from "./Hand";
import Progress from "./Progress";
import axios from "axios";
import { useParams } from "react-router-dom";
import Instructions from "./Instructions";

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
            setSelectedCardIndex(null);
          }).catch((error) => {
            console.error('Error:', error);
          });
    }

    const discard = () => {
        axios.post(`/api/${code}/discard`, null, {
          params: {
            index: selectedCardIndex
        }
        }).then((response) => {
            setGame(response.data);
            setSelectedCardIndex(null);
          }).catch((error) => {
            console.error('Error:', error);
          });
    }

    const chooseProgressToken = (token) => {
      axios.post(`/api/${code}/chooseProgressToken`, null, {
        params: {
          progressToken: token
        }
      }).then((response) => {
        setGame(response.data);
      }).catch((error) => {
        console.error('Error:', error);
      })
    }

      function constructWonder() {
        console.log(selectedCardIndex + " was used to construct a wonder");
        setSelectedCardIndex(null);
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
            <Progress chooseScience={game.step ==="CHOOSE_SCIENCE"} military={game.military} tokensAvailable={game.tokensAvailable} onTokenClick={chooseProgressToken}></Progress>
            <Board cardSetter={setSelectedCardIndex} age={game.age} cards={game.visiblePyramid} selectedCardIndex={selectedCardIndex}></Board>
          </div>
          <div className="gameInner2">
            <div className="playerMoves">
              <Instructions step={game.step} currentPlayerNumber={game.currentPlayerNumber} cardSelected={selectedCardIndex}></Instructions>
              {selectedCardIndex != null && game.step === "PLAY_CARD"?
              (<div><Button text="Construct the Building" onClick={constructBuilding}></Button>
              <Button text="Discard the card to obtain coins" onClick={discard}></Button></div>) :null}
            </div>
            <Hand num={1} sortedHand={game.player1.sortedHand} money={game.player1.money} tokens={game.player1.tokens}></Hand>
            <Hand num={2} sortedHand={game.player2.sortedHand} money={game.player2.money} tokens={game.player2.tokens}></Hand>
          </div>
        </div>)
        );
}