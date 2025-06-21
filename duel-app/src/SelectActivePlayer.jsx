import Button from "./Button";
import './SelectActivePlayer.css';

export default function SelectActivePlayer({handleSetActivePlayer}) {

    return (
    <div className="overlay">
        <div className="set-active">
            Which player are you?
            <Button onClick={() => {handleSetActivePlayer(1)}} text="Player 1"/>
            <Button onClick={() => {handleSetActivePlayer(2)}} text="Player 2"/>
        </div>
    </div>)
}