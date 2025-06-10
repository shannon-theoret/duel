import Button from "./Button"
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { API_BASE_URL } from './config';

export default function NewGame() {

    let navigate = useNavigate();

    const newGame = () => {
        axios.post(`${API_BASE_URL}/newGame`).then((response) => {
            navigate(`/${response.data.code}`);
        }).catch((error) => {
          console.error('Error:', error);
        });
    }

    return <Button className="newGame" text="New Game" onClick={newGame}></Button>;
}