import { useState } from 'react';
import './Tooltip.css';
import { useContext } from 'react';
import { SettingsContext } from './SettingsContext';

export default function Tooltip({ textKey, cName, children }) {
    const [visible, setVisible] = useState(false);
    const { helpMode } = useContext(SettingsContext);

    const helpMap = {
        THE_APPIAN_WAY: { title: "The Appian Way", helpText: "You take 3 coins from the bank. Your opponent loses 3 coins, which are returned to the bank. Immediately play a second turn. This Wonder is worth 3 victory points." },
        CIRCUS_MAXIMUS: { title: "Circus Maximus", helpText: "Place in the discard pile a grey card (manufactured goods) of your choice constructed by your opponent. This Wonder is worth 1 Shield. This Wonder is worth 3 victory points." },
        THE_COLOSSUS: { title: "The Colossus", helpText: "This Wonder is worth 2 Shields. This Wonder is worth 3 victory points." },
        THE_GREAT_LIBRARY: { title: "The Great Library", helpText: "Randomly draw 3 Progress tokens from among those discarded at the beginning of the game. Choose one, play it, and return the other 2 to the box. This Wonder is worth 4 victory points." },
        THE_GREAT_LIGHTHOUSE: { title: "The Great Lighthouse", helpText: "This Wonder produces one unit of the resources shown (Stone, Clay, or Wood) for you each turn. Clarification: This production has no impact on the cost of trading. This Wonder is worth 4 victory points." },
        THE_HANGING_GARDENS: { title: "The Hanging Gardens", helpText: "You take 6 coins from the bank. Immediately play a second turn. This Wonder is worth 3 victory points." },
        THE_MAUSOLEUM: { title: "The Mausoleum", helpText: "Take all of the cards which have been discarded since the beginning of the game and immediately construct one of your choice for free. Clarification: The cards discarded during setup are not part of the discard. This Wonder is worth 2 victory points." },
        PIRAEUS: { title: "Piraeus", helpText: "This Wonder produces one unit of one of the resources shown (Glass or Papyrus) for you each turn. Clarification: This production has no impact on the cost of trading. Immediately play a second turn. This Wonder is worth 2 victory points." },
        THE_PYRAMIDS: { title: "The Pyramids", helpText: "This Wonder is worth 9 victory points." },
        THE_SPHINX: { title: "The Sphinx", helpText: "Immediately play a second turn. This Wonder is worth 6 victory points." },
        THE_STATUE_OF_ZEUS: { title: "The Statue of Zeus", helpText: "Put in the discard pile one brown card (Raw goods) of your choice constructed by their opponent. This Wonder is worth 1 Shield. This Wonder is worth 3 victory points." },
        THE_TEMPLE_OF_ARTEMIS: { title: "The Temple of Artemis", helpText: "Immediately take 12 coins from the Bank. Immediately play a second turn." },
        AGRICULTURE: { title: "Agriculture", helpText: "Immediately take 6 coins from the Bank. The token is worth 4 victory points." },
        ARCHITECTURE: { title: "Architecture", helpText: "Any future Wonders built by you will cost 2 fewer resources. At each construction, you are free to choose which resources this rebate affects." },
        ECONOMY: { title: "Economy", helpText: "You gain the money spent by your opponent when they trade for resources." },
        LAW: { title: "Law", helpText: "This token is worth a scientific symbol." },
        MASONRY: { title: "Masonry", helpText: "Any future civilian Buildings (blue cards) constructed by you will cost 2 fewer resources. At each construction, you are free to choose which resources this rebate affects." },
        MATHEMATICS: { title: "Mathematics", helpText: "At the end of the game, score 3 victory points for each Progress token in your possession (including itself)." },
        PHILOSOPHY: { title: "Philosophy", helpText: "The token is worth 7 victory points." },
        STRATEGY: { title: "Strategy", helpText: "Once this token enters play, your new military Buildings (red cards) will benefit from 1 extra Shield. Example: a military Building with 2 shields" },
        THEOLOGY: { title: "Theology", helpText: "All future Wonders constructed by you are all treated as though they have the “Play Again” effect." },
        URBANISM: { title: "Urbanism", helpText: "Immediately take 6 coins from the Bank. Each time you construct a Building for free through linking (free construction condition, chain), you gain 4 coins." }
    }

    return (
        <div 
            onMouseEnter={() => setVisible(true)} 
            onMouseLeave={() => setVisible(false)}
            className={cName ? cName : "tooltip-container"} 
        >
            {children}
            {visible && helpMode && (
                <div className="tooltip">
                    <div><b>{helpMap[textKey].title}</b></div>
                    <div>{helpMap[textKey].helpText}</div>
                </div>
            )}
        </div>
    );
}
