import './App.css';
import Game from './Game';
import NewGame from './NewGame';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
  return (
    <div className='App'>
      <Router>
        <Routes>
          <Route exact path="/" element={<NewGame/>}/>
          <Route path="/:code" element={<Game/>}/>
        </Routes>
      </Router>
    </div>
  );
}

export default App;