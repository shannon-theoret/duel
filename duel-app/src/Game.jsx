import { useState, useEffect } from "react";
import Board from "./Board";
import Button from "./Button";
import './Game.css';
import Hand from "./Hand";
import Progress from "./Progress";
import axios from "axios";
import { useParams } from "react-router-dom";
import Instructions from "./Instructions";
import WonderSelection from "./WonderSelection";
import ErrorBox from "./ErrorBox"
import Collapsible from "./Collapsible";
import DiscardPile from "./DiscardPile";
import Tokens from "./Tokens";

export default function Game() {

    const [selectedCardIndex, setSelectedCardIndex] = useState(null);
    const [errorMessage, setErrorMessage] = useState("");
    const [seeDiscard, setSeeDiscard] = useState(false);

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
    }, []);

    const constructBuilding = () => {
          axios.post(`/api/${code}/constructBuilding`, null, {
            params: {
              index: selectedCardIndex
          }
          }).then((response) => {
            setGame(response.data);
            setErrorMessage("");
            setSelectedCardIndex(null);
          }).catch((error) => {
            setErrorMessage(error.response?.data || "An unknown error occurred.");
          });
    }

    const constructFromDiscard = (cardName) => {
      axios.post(`/api/${code}/constructBuildingFromDiscard`, null, {
        params: {
          cardName: cardName
      }
      }).then((response) => {
        setGame(response.data);
        setErrorMessage("");
      }).catch((error) => {
        setErrorMessage(error.response?.data || "An unknown error occurred.");
      });
    }

    const discard = () => {
        axios.post(`/api/${code}/discard`, null, {
          params: {
            index: selectedCardIndex
        }
        }).then((response) => {
            setGame(response.data);
            setErrorMessage("");
            setSelectedCardIndex(null);
          }).catch((error) => {
            setErrorMessage(error.response?.data || "An unknown error occurred.");
          });
    }

    const chooseProgressToken = (token) => {
      axios.post(`/api/${code}/chooseProgressToken`, null, {
        params: {
          progressToken: token
        }
      }).then((response) => {
        setGame(response.data);
        setErrorMessage("");
      }).catch((error) => {
        setErrorMessage(error.response?.data || "An unknown error occurred.");
      })
    }

    const chooseProgressTokenFromDiscard = (token) => {
      axios.post(`/api/${code}/chooseProgressTokenFromDiscard`, null, {
        params: {
          progressToken: token
        }
      }).then((response) => {
        setGame(response.data);
        setErrorMessage("");
      }).catch((error) => {
        setErrorMessage(error.response?.data || "An unknown error occurred.");
      })
    }

    const selectWonder = (wonder) => {
      axios.post(`/api/${code}/selectWonder`, null, {
        params: {
          wonder: wonder
        }
      }).then((response) => {
        setGame(response.data);
        setErrorMessage("");
      }).catch((error) => {
        setErrorMessage(error.response?.data || "An unknown error occurred.");
      })
    }

    const constructWonder = (wonder) => {
      axios.post(`/api/${code}/constructWonder`, null, {
        params: {
          index: selectedCardIndex,
          wonder: wonder
        }
      }).then((response) => {
        setGame(response.data);
        setErrorMessage("");
      }).catch((error) => {
        setErrorMessage(error.response?.data || "An unknown error occurred.");
      })
    }

    const destroyCard = (cardName) => {
      axios.post(`/api/${code}/destroyCard`, null, {
        params: {
          cardName: cardName
        }
      }).then((response) => {
        setGame(response.data);
        setErrorMessage("");
      }).catch((error) => {
        setErrorMessage(error.response?.data || "An unknown error occurred.");
      })
    }


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
          <div className="gameInner1">
            {game.step === "CHOOSE_PROGRESS_TOKEN_FROM_DISCARD" &&
            <Tokens tokens={game.tokensFromUnavailable} onClickToken={chooseProgressTokenFromDiscard}></Tokens>}
            <Progress chooseScience={game.step ==="CHOOSE_PROGRESS_TOKEN"} military={game.military} tokensAvailable={game.tokensAvailable} onTokenClick={chooseProgressToken}></Progress>
            <Collapsible label="See all discarded card" defaultOpen={game.step === "CONSTRUCT_FROM_DISCARD"}>
              <DiscardPile cards={game.discardedCards} constructFromDiscard={game.step == "CONSTRUCT_FROM_DISCARD"? constructFromDiscard : null}></DiscardPile>
            </Collapsible>
            <Board cardSetter={setSelectedCardIndex} age={game.age} cards={game.visiblePyramid} selectedCardIndex={selectedCardIndex}></Board>
          </div>
          <div className="gameInner2">
            <div className="playerMoves">
              <Instructions step={game.step} currentPlayerNumber={game.currentPlayerNumber} cardSelected={selectedCardIndex}></Instructions>
              {game.step ==="WONDER_SELECTION"?
              (<WonderSelection wonders={game.wondersAvailable} selectWonder={selectWonder}></WonderSelection>) : null}
              {selectedCardIndex !== null && game.step === "PLAY_CARD"?
              (<div><Button text="Construct the Building" onClick={constructBuilding}></Button>
              <Button text="Discard the card to obtain coins" onClick={discard}></Button></div>) :null}
            </div>
            <Collapsible label="Player 1 Hand" defaultOpen={game.currentPlayerNumber===1 || game.step === "DESTROY_BROWN" || game.step === "DESTROY_GREY" || game.step === "WONDER_SELECTION"}>
              <Hand 
                sortedHand={game.player1.sortedHand} 
                money={game.player1.money} 
                wonders={game.player1.wonders} 
                tokens={game.player1.tokens} 
                onClickWonder={game.currentPlayerNumber===1 && game.step === "PLAY_CARD" && selectedCardIndex !== null? constructWonder: null} 
                destroyCard={(game.step === "DESTROY_GREY" || game.step === "DESTROY_BROWN") && game.currentPlayerNumber===2? destroyCard : null} 
              />
            </Collapsible>
            <Collapsible label="Player 2 Hand" defaultOpen={game.currentPlayerNumber===2 || game.step === "DESTROY_BROWN" || game.step === "DESTROY_GREY" || game.step === "WONDER_SELECTION"}>
              <Hand 
                sortedHand={game.player2.sortedHand} 
                money={game.player2.money} 
                wonders={game.player2.wonders} 
                tokens={game.player2.tokens} 
                onClickWonder={game.currentPlayerNumber===2 && game.step === "PLAY_CARD" && selectedCardIndex !== null? constructWonder: null}
                destroyCard={(game.step === "DESTROY_GREY" || game.step === "DESTROY_BROWN") && game.currentPlayerNumber===1? destroyCard : null} 
              />
            </Collapsible>  
          </div>
        </div>)}
        </>);
}