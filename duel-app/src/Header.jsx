import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { SettingsContext } from './SettingsContext';
import Slider from './Slider';

export default function Header() {
    const [inputtedGameCode, setInputtedGameCode] = useState('');
    const navigate = useNavigate();
    const { helpMode, setHelpMode, autoOpenPlayerHand, setAutoOpenPlayerHand } = useContext(SettingsContext);
  
    const newGame = () => {
        axios.post('/api/newGame').then((response) => {
            navigate(`/${response.data.code}`);
        }).catch((error) => {
          console.error('Error:', error);
        });
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