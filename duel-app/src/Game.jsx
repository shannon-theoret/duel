import { useState, useEffect, useContext } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import './Game.css';
import ErrorBox from "./ErrorBox";
import Collapsible from "./Collapsible";
import Hand from "./Hand";
import PlayerMoves from "./PlayerMoves";
import GameBoard from "./GameBoard";
import { SettingsContext } from './SettingsContext';
import Score from "./Score";
import { API_BASE_URL } from './config';
import LoadingOverlay from "./LoadingOverlay";

export default function Game() {
    const [selectedCardIndex, setSelectedCardIndex] = useState(null);
    const [errorMessage, setErrorMessage] = useState("");
    const { code } = useParams();
    const [game, setGame] = useState({
        "code": code,
        "step": "SETUP"
    });
    const [waitingForAI, setWaitingForAI] = useState(false);
    const { autoOpenPlayerHand } = useContext(SettingsContext);

    useEffect(() => {
      axios.get(`${API_BASE_URL}/${code}`).then((response) => {
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

    const handleMakeAiMove = () => {
      setWaitingForAI(true);
      axios.post(`${API_BASE_URL}/${code}/makeAIMove`)
        .then(response => {
          setGame(response.data);
          setErrorMessage("");
          setWaitingForAI(false);
        })
        .catch(error => {
          setErrorMessage(error.response?.data || "An unknown error occurred.");
          setWaitingForAI(false);
        });
    };


    const handleConstructBuilding = () => {
      executePlayerMove(`${API_BASE_URL}/${code}/constructBuilding`, { index: selectedCardIndex });
      setSelectedCardIndex(null);
    }

    const handleConstructFromDiscard = (cardName) => {
      executePlayerMove(`${API_BASE_URL}/${code}/constructBuildingFromDiscard`, { cardName });
    }

    const handleDiscard = () => {
      executePlayerMove(`${API_BASE_URL}/${code}/discard`, { index: selectedCardIndex });
      setSelectedCardIndex(null);
    }

    const handleChooseProgressToken = (token) => {
      executePlayerMove(`${API_BASE_URL}/${code}/chooseProgressToken`, { progressToken: token });
    }

    const handleChooseProgressTokenFromDiscard = (token) => {
      executePlayerMove(`${API_BASE_URL}/${code}/chooseProgressTokenFromDiscard`, { progressToken: token });
    }

    const handleSelectWonder = (wonder) => {
      executePlayerMove(`${API_BASE_URL}/${code}/selectWonder`, { wonder });
    }

    const handleConstructWonder = (wonder) => {
      executePlayerMove(`${API_BASE_URL}/${code}/constructWonder`, { index: selectedCardIndex, wonder });
    }

    const handleDestroyCard = (cardName) => {
      executePlayerMove(`${API_BASE_URL}/${code}/destroyCard`, { cardName });
    }

    return (
      <>
      {errorMessage && <ErrorBox errorMessage={errorMessage}></ErrorBox>}
        {game.step !== "SETUP" && (
        <div className="game">
          <div className="game-inner1">
          {game.step === "GAME_END" &&
              (<Score
              player1Score={game.player1.score}
              player2Score={game.player2.score}
              player1WinStatus={game.player1.winStatus}
              player2WinStatus={game.player2.winStatus}
              />
            )}
            <GameBoard 
              game={game} 
              setSelectedCardIndex={setSelectedCardIndex} 
              selectedCardIndex={selectedCardIndex} 
              handleChooseProgressTokenFromDiscard={handleChooseProgressTokenFromDiscard} 
              handleChooseProgressToken={handleChooseProgressToken} 
              handleConstructFromDiscard={handleConstructFromDiscard} 
            />
          </div>  
          <div className="game-inner2">
            {waitingForAI? (<LoadingOverlay />) :
            (<PlayerMoves 
              game={game} 
              selectedCardIndex={selectedCardIndex}
              handleConstructBuilding={handleConstructBuilding} 
              handleDiscard={handleDiscard} 
              handleSelectWonder={handleSelectWonder} 
              handleConstructWonder={handleConstructWonder} 
              handleDestroyCard={handleDestroyCard} 
              handleMakeAiMove={handleMakeAiMove}
            />)}
            <Collapsible label="PLAYER 1 CITY" defaultOpen={!autoOpenPlayerHand || game.currentPlayerNumber===1 || game.step === "DESTROY_BROWN" || game.step === "DESTROY_GREY" || game.step === "WONDER_SELECTION"}>
              <Hand 
                sortedHand={game.player1?.sortedHand} 
                money={game.player1?.money} 
                wonders={game.player1?.wonders} 
                tokens={game.player1?.tokens} 
                onClickWonder={game.currentPlayerNumber===1 && game.step === "PLAY_CARD" && selectedCardIndex !== null? handleConstructWonder: null} 
                destroyCard={(game.step === "DESTROY_GREY" || game.step === "DESTROY_BROWN") && game.currentPlayerNumber===2? handleDestroyCard : null} 
              />
            </Collapsible>
            <Collapsible label="PLAYER 2 CITY" defaultOpen={!autoOpenPlayerHand || game.currentPlayerNumber===2 || game.step === "DESTROY_BROWN" || game.step === "DESTROY_GREY" || game.step === "WONDER_SELECTION"}>
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