import './App.css';
import Game from './Game';
import NewGame from './NewGame';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './Header';
import { SettingsProvider } from './SettingsContext';
import axios from 'axios';
import { useEffect } from 'react';

function App() {
  
  useEffect(() => {
    axios.get(`${import.meta.env.VITE_API_BASE_URL}/ping`)
      .catch((err) => {
        console.warn('Warm-up ping failed:', err);
      });
  }, []);

  return (
    <Router>
      <div className='App'>
        <SettingsProvider>
          <Header />
          <Routes>
            <Route path="/" element={<NewGame />} />
            <Route path="/:code" element={<Game />} />
          </Routes>
        </SettingsProvider>
      </div>
    </Router>
  );
}

export default App;