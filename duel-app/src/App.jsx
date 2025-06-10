import './App.css';
import Game from './Game';
import NewGame from './NewGame';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './Header';
import GameWrapper from './GameWrapper';
import { SettingsProvider } from './SettingsContext';

function App() {
  return (
    <Router>
      <div className='App'>
        <SettingsProvider>
          <Header />
          <Routes>
            <Route path="/:code" element={<GameWrapper />} />
          </Routes>
        </SettingsProvider>
      </div>
    </Router>
  );
}

export default App;