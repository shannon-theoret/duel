import { useParams } from 'react-router-dom';
import Game from './Game';

export default function GameWrapper() {
  const { code } = useParams();
  return <Game key={code} />;
};