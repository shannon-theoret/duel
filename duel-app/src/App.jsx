import './App.css';
import Game from './Game';
import NewGame from './NewGame';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './Header';
import GameWrapper from './GameWrapper';

function App() {
  return (
    <Router>
      <div className='App'>
        <Header />
        <Routes>
          <Route exact path="/" element={<NewGame />} />
          <Route path="/:code" element={<GameWrapper />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;