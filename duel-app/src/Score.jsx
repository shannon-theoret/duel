import './Score.css';

export default function Score({player1Score, player2Score, player1WinStatus, player2WinStatus}) {
        const player1TotalScore = Object.values(player1Score).reduce((sum, value) => sum + value, 0);
        const player2TotalScore = Object.values(player2Score).reduce((sum, value) => sum + value, 0);

        return (
            <div className='score-overlay'>
                {player1WinStatus == "CIVILIAN_VICTORY" && 
                    (<div className='win-summary'>Player 1 has won with {player1TotalScore} points</div>)}
                {player1WinStatus == "SCIENCE_VICTORY" &&
                    (<div className='win-summary'>Player 1 has won through Scientific Supremacy</div>)}
                {player1WinStatus == "MILITARY_VICTORY" &&
                    (<div className='win-summary'>Player 1 has won through Military Supremacy</div>)}       
                {player2WinStatus == "CIVILIAN_VICTORY" &&
                    (<div className='win-summary'>Player 2 has won with {player2TotalScore} points</div>)}
                {player2WinStatus == "SCIENCE_VICTORY" &&
                    (<div className='win-summary'>Player 2 has won through Scientific Supremacy</div>)}
                {player2WinStatus == "MILITARY_VICTORY" &&
                    (<div className='win-summary'>Player 2 has won through Military Supremacy</div>)}
                {(player1WinStatus == "CIVILIAN_VICTORY" || player2WinStatus == "CIVILIAN_VICTORY") && (            
                <table className="score">
                    <thead>
                        <tr>
                            <th>Category</th>
                            <th>Player 1</th>
                            <th>Player 2</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr className="civilianScore">
                            <th>Civilian</th>
                            <td>{player1Score["CIVILIAN_BUILDING"]}</td>
                            <td>{player2Score["CIVILIAN_BUILDING"]}</td>
                        </tr>
                        <tr className="scienceScore">
                            <th>Science</th>
                            <td>{player1Score["SCIENTIFIC_BUILDING"]}</td>
                            <td>{player2Score["SCIENTIFIC_BUILDING"]}</td>
                        </tr>
                        <tr className="commercialScore">
                            <th>Commercial</th>
                            <td>{player1Score["COMMERCIAL_BUILDING"]}</td>
                            <td>{player2Score["COMMERCIAL_BUILDING"]}</td>
                        </tr>
                        <tr className="guildScore">
                            <th>Guild</th>
                            <td>{player1Score["GUILD"]}</td>
                            <td>{player2Score["GUILD"]}</td>
                        </tr>
                        <tr className="wonderScore">
                            <th>Wonder</th>
                            <td>{player1Score["WONDER"]}</td>
                            <td>{player2Score["WONDER"]}</td>
                        </tr>
                        <tr className="progressScore">
                            <th>Progress Tokens</th>
                            <td>{player1Score["PROGRESS_TOKEN"]}</td>
                            <td>{player2Score["PROGRESS_TOKEN"]}</td>
                        </tr>
                        <tr className="moneyScore">
                            <th>Coins</th>
                            <td>{player1Score["MONEY"]}</td>
                            <td>{player2Score["MONEY"]}</td>
                        </tr>
                        <tr className="militaryScore">
                            <th>Military</th>
                            <td>{player1Score["MILITARY_BUILDING"]}</td>
                            <td>{player2Score["MILITARY_BUILDING"]}</td>
                        </tr>
                        <tr className='totalScore'>
                            <th>Total</th>
                            <td>{player1TotalScore}</td>
                            <td>{player2TotalScore}</td>
                        </tr>
                    </tbody>
                </table>
                )}
            </div>    
        )
    }    
