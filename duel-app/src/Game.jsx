import { useState, useEffect, useContext, useRef } from "react";
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
import SelectActivePlayer from "./SelectActivePlayer";
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export default function Game() {
    const [selectedCardIndex, setSelectedCardIndex] = useState(null);
    const [errorMessage, setErrorMessage] = useState("");
    const { code } = useParams();
    const [game, setGame] = useState({
        "code": code,
        "step": "SETUP"
    });
    const [waitingForAI, setWaitingForAI] = useState(false);
    const hasRequestedAIMove = useRef(false);
    const { autoOpenPlayerHand } = useContext(SettingsContext);
    const [activePlayer, setActivePLayer] = useState(() => {
      return localStorage.getItem(`playerId-${code}`) || '';
    });
    const isActivePlayersTurn = activePlayer == game.currentPlayerNumber;

    const AI_PLAYER = 2;

    useEffect(() => {
      axios.get(`${API_BASE_URL}/${code}`).then((response) => {
          setGame(response.data);
          const storedPlayerNum = localStorage.getItem(`playerId-${code}`) || '';
          setActivePLayer(storedPlayerNum);
      }).catch((error) => {
        setErrorMessage(error.response?.data || "An unknown error occurred.");
      });
    }, [code]);

    useEffect(() => {
          const socket = new SockJS(`${API_BASE_URL}/ws`);
            const stompClient = new Client({
                webSocketFactory: () => socket,
                reconnectDelay: 5000,
                heartbeatIncoming: 10000,
                heartbeatOutgoing: 10000,
                onConnect: () => {
                    stompClient.subscribe(`/topic/games/${code}`, message => {
                        const updatedGame = JSON.parse(message.body);
                        setGame(updatedGame);
                    });
                },
                onStompError: (frame) => {
                    console.error("STOMP error", frame.headers["message"], frame.body);
                }
            });
        
            stompClient.activate();
        
            return () => {
                stompClient.deactivate();
            };
    }, [code]);

    useEffect(() => {
      if (game.currentPlayerNumber == AI_PLAYER && game.player2.ai && !waitingForAI && !hasRequestedAIMove.current && game.step !== 'GAME_END') {
        handleMakeAiMove();
      }
    }, [game]);

    const executePlayerMove = (url, params) => {
      if (isActivePlayersTurn) {
        axios.post(url, null, { params })
        .then(response => {
            setGame(response.data);
            setErrorMessage("");
        })
        .catch(error => setErrorMessage(error.response?.data || "An unknown error occurred."));
      }
    }

    const handleMakeAiMove = () => {
      setWaitingForAI(true);
      hasRequestedAIMove.current = true;
      axios.post(`${API_BASE_URL}/${code}/makeAIMove`)
        .then(response => {
          setGame(response.data);
          setErrorMessage("");
          setWaitingForAI(false);
          hasRequestedAIMove.current = false; 
        })
        .catch(error => {
          setErrorMessage(error.response?.data || "An unknown error occurred.");
          setWaitingForAI(false);
          setAllowPlayingForOpponent(false);
          hasRequestedAIMove.current = false; 
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

    const handleSetActivePlayer = (num) => {
      setActivePLayer(num);
      localStorage.setItem(`playerId-${code}`, num);
    }

    return (
      <>
      {errorMessage && <ErrorBox errorMessage={errorMessage}></ErrorBox>}
        {game.step !== "SETUP" && (
        <div className="game">
          <div className="game-inner-wrapper">
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
              isActivePlayersTurn={isActivePlayersTurn} 
              setSelectedCardIndex={setSelectedCardIndex} 
              selectedCardIndex={selectedCardIndex} 
              handleChooseProgressTokenFromDiscard={handleChooseProgressTokenFromDiscard} 
              handleChooseProgressToken={handleChooseProgressToken} 
              handleConstructFromDiscard={handleConstructFromDiscard} 
            />
          </div>  
          <div className="game-inner2">
            {!isActivePlayersTurn && activePlayer && <LoadingOverlay />}
            {(activePlayer == '') && <SelectActivePlayer handleSetActivePlayer={handleSetActivePlayer}></SelectActivePlayer>}
            <>
            <PlayerMoves 
              game={game} 
              selectedCardIndex={selectedCardIndex}
              handleConstructBuilding={handleConstructBuilding} 
              handleDiscard={handleDiscard} 
              handleSelectWonder={handleSelectWonder} 
              handleConstructWonder={handleConstructWonder} 
              handleDestroyCard={handleDestroyCard} 
              handleMakeAiMove={handleMakeAiMove}
              isActivePlayersTurn={isActivePlayersTurn}
            />
            
            </>
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
          </div>
        </div>)}
        </>);
}