import { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import './Game.css';
import ErrorBox from "./ErrorBox";
import Collapsible from "./Collapsible";
import Hand from "./Hand";
import PlayerMoves from "./PlayerMoves";
import GameBoard from "./GameBoard";

export default function Game() {

    const [selectedCardIndex, setSelectedCardIndex] = useState(null);
    const [errorMessage, setErrorMessage] = useState("");
    const {code} = useParams();
    const [game, setGame] = useState({
        "code": code,
        "step": "SETUP"
    })

    useEffect(() => {
      axios.get(`/api/${code}`).then((response) => {
          setGame(response.data);
      }).catch((error) => {
        setErrorMessage(error.response?.data || "An unknown error occurred.");
      });
    }, [code]);

    const executePlayerMove = (url, params) => {
      axios.post(url, null, { params })
            .then(response => {
                setGame(response.data);
                setErrorMessage("");
            })
            .catch(error => setErrorMessage(error.response?.data || "An unknown error occurred."));
    }

    const handleConstructBuilding = () => {
      executePlayerMove(`/api/${code}/constructBuilding`, { index: selectedCardIndex });
      setSelectedCardIndex(null);
    }

    const handleConstructFromDiscard = (cardName) => {
      executePlayerMove(`/api/${code}/constructBuildingFromDiscard`, { cardName });
    }

    const handleDiscard = () => {
      executePlayerMove(`/api/${code}/discard`, { index: selectedCardIndex });
      setSelectedCardIndex(null);
    }

    const handleChooseProgressToken = (token) => {
      executePlayerMove(`/api/${code}/chooseProgressToken`, { progressToken: token });
    }

    const handleChooseProgressTokenFromDiscard = (token) => {
      executePlayerMove(`/api/${code}/chooseProgressTokenFromDiscard`, { progressToken: token });
    }

    const handleSelectWonder = (wonder) => {
      executePlayerMove(`/api/${code}/selectWonder`, { wonder });
    }

    const handleConstructWonder = (wonder) => {
      executePlayerMove(`/api/${code}/constructWonder`, { index: selectedCardIndex, wonder });
    }

    const handleDestroyCard = (cardName) => {
      executePlayerMove(`/api/${code}/destroyCard`, { cardName });
    }

    //TODO:REMOVEME
      function testStuff() {
          axios.post(`/api/${code}/testStuff`).then((response) => {
            setGame(response.data);
            setErrorMessage("");
          }).catch((error) => {
            setErrorMessage(error.response?.data || "An unknown error occurred.");
          });
      }

    return (
      <>
      {errorMessage && <ErrorBox errorMessage={errorMessage}></ErrorBox>}
        {game.step !== "SETUP" && (
        <div className="game">  
          <GameBoard 
            game={game} 
            setSelectedCardIndex={setSelectedCardIndex} 
            selectedCardIndex={selectedCardIndex} 
            handleChooseProgressTokenFromDiscard={handleChooseProgressTokenFromDiscard} 
            handleChooseProgressToken={handleChooseProgressToken} 
            handleConstructFromDiscard={handleConstructFromDiscard} 
          />
          <div className="gameInner2">
            <PlayerMoves 
              game={game} 
              selectedCardIndex={selectedCardIndex} 
              handleConstructBuilding={handleConstructBuilding} 
              handleDiscard={handleDiscard} 
              handleSelectWonder={handleSelectWonder} 
              handleConstructWonder={handleConstructWonder} 
              handleDestroyCard={handleDestroyCard} 
            />
            <Collapsible label="Player 1 Hand" defaultOpen={game.currentPlayerNumber===1 || game.step === "DESTROY_BROWN" || game.step === "DESTROY_GREY" || game.step === "WONDER_SELECTION"}>
              <Hand 
                sortedHand={game.player1?.sortedHand} 
                money={game.player1?.money} 
                wonders={game.player1?.wonders} 
                tokens={game.player1?.tokens} 
                onClickWonder={game.currentPlayerNumber===1 && game.step === "PLAY_CARD" && selectedCardIndex !== null? handleConstructWonder: null} 
                destroyCard={(game.step === "DESTROY_GREY" || game.step === "DESTROY_BROWN") && game.currentPlayerNumber===2? handleDestroyCard : null} 
              />
            </Collapsible>
            <Collapsible label="Player 2 Hand" defaultOpen={game.currentPlayerNumber===2 || game.step === "DESTROY_BROWN" || game.step === "DESTROY_GREY" || game.step === "WONDER_SELECTION"}>
              <Hand 
                sortedHand={game.player2?.sortedHand} 
                money={game.player2?.money} 
                wonders={game.player2?.wonders} 
                tokens={game.player2?.tokens} 
                onClickWonder={game.currentPlayerNumber===2 && game.step === "PLAY_CARD" && selectedCardIndex !== null? handleConstructWonder: null}
                destroyCard={(game.step === "DESTROY_GREY" || game.step === "DESTROY_BROWN") && game.currentPlayerNumber===1? handleDestroyCard : null} 
              />
            </Collapsible>  
          </div>
        </div>)}
        </>);
}