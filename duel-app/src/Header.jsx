import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { SettingsContext } from './SettingsContext';
import Slider from './Slider';
import './Header.css';
import { API_BASE_URL } from './config';


export default function Header() {
    const [inputtedGameCode, setInputtedGameCode] = useState('');
    const navigate = useNavigate();
    const { helpMode, setHelpMode, autoOpenPlayerHand, setAutoOpenPlayerHand } = useContext(SettingsContext);
  
    const newGame = () => {
        navigate(`/`);
    }

    const handleOpenGame = () => {
      if (inputtedGameCode.trim()) {
        navigate(`/${inputtedGameCode}`);
      }
    };

    return (
        <header>
            <span className='title'>7 Wonders Duel</span>
        <nav>
        <span className="link" onClick={newGame}>NEW GAME</span>
        <span>|</span>
        <span className="link" onClick={handleOpenGame}>OPEN GAME</span>
        <input
          type="text"
          placeholder="Enter Game Code"
          value={inputtedGameCode}
          onChange={(e) => setInputtedGameCode(e.target.value)}
        />
      </nav>
      <Slider label="Help Mode" isChecked={helpMode} toggleFunction={setHelpMode}></Slider>
          <Slider label="Auto Open/Close Player Hand" isChecked={autoOpenPlayerHand} toggleFunction={setAutoOpenPlayerHand}></Slider>

        </header>
    );
}