import './WonderSelection.css';
import Wonder from "./Wonder";

export default function WonderSelection({wonders, selectWonder}) {
    return (
        <div className="wonders">
            {wonders.map(wonder => (
                <Wonder key={wonder} wonderName={wonder} onClickWonder={() => selectWonder(wonder)}></Wonder>
            ))}
        </div>
    )
}