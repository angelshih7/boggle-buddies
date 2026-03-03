import { useLocation } from 'react-router-dom';
import './GamePage.css';

// Placeholder letters used only during local development when no board
// is received from the backend. Replace with an API fetch once the
// board endpoint is available (backend: ShuffleUtil.shuffle_board()).
const DEV_PLACEHOLDER = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'];

export default function GamePage() {
  const location = useLocation();
  const playerName = location.state?.playerName ?? 'Guest';
  const score      = location.state?.score      ?? 0;

  // Board letters come from the backend (via navigation state or future API call).
  // Expected shape: flat array of 16 strings, e.g. ["A","T","Qu","R", ...]
  const letters = location.state?.letters ?? DEV_PLACEHOLDER;

  return (
    <div className="game-page">
      <aside className="game-sidebar">
        <div className="player-avatar">
          {playerName.charAt(0).toUpperCase()}
        </div>
        <h2 className="player-name">{playerName}</h2>
        <div className="score-section">
          <span className="score-label">Score</span>
          <span className="score-value">{score}</span>
        </div>
      </aside>

      <main className="game-main">
        <div className="boggle-grid">
          {letters.map((letter, i) => (
            <div key={i} className="tile">
              <span className={`tile-letter${letter === 'Qu' ? ' tile-letter--qu' : ''}`}>
                {letter}
              </span>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}
