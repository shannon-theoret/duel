import Button from "./Button"
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { API_BASE_URL } from './config';
import AiLevelSelect from "./AiLevelSelect";
import { useState } from "react";
import './NewGame.css';

export default function NewGame() {

    const navigate = useNavigate();
    const [aiOpponent, setAiOpponent] = useState(false);
    const [aiLevel, setAiLevel] = useState(1);

    const newGame = () => {
        axios.post(`${API_BASE_URL}/newGame`, null, {
        params: {
          aiOpponent: aiOpponent,  
          level: aiLevel
        }
        }).then((response) => {
            localStorage.setItem(`playerId-${response.data.code}`, 1);
            navigate(`/${response.data.code}`);
        }).catch((error) => {
          console.error('Error:', error);
        });
    }


    return (<div className="new-game-setup">
        <label>
        <input
            type="checkbox"
            checked={aiOpponent}
            onChange={(e) => setAiOpponent(e.target.checked)}
        />
            AI Opponent
        </label>
        {aiOpponent && (<AiLevelSelect
            aiLevel={aiLevel}
            setAiLevel={setAiLevel}
        ></AiLevelSelect>)}
        <Button className="newGame" text="New Game" onClick={newGame}></Button>
    </div>);
}