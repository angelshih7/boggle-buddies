import { useCallback, useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { clearExpiredSession } from '../utils/session';
import './GamePage.css';

/**
 * Placeholder letters used only during local development.
 */
const DEV_PLACEHOLDER = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P'];
const GRID_SIZE = 4;
const MIN_WORD_LENGTH = 3;

// --- Adjacency helpers -------------------------------------------------

function tileToRowCol(index) {
  return { row: Math.floor(index / GRID_SIZE), col: index % GRID_SIZE };
}

function isAdjacent(a, b) {
  const { row: r1, col: c1 } = tileToRowCol(a);
  const { row: r2, col: c2 } = tileToRowCol(b);
  return a !== b && Math.abs(r1 - r2) <= 1 && Math.abs(c1 - c2) <= 1;
}

const RULES = [
  { heading: 'Adjacent tiles only', body: 'Connect tiles horizontally, vertically, or diagonally.' },
  { heading: 'No reusing tiles', body: 'Each tile can only be used once per word. Backtrack to undo.' },
  { heading: 'Minimum 3 letters', body: 'Words shorter than 3 letters are not accepted.' },
  { heading: 'Must be in the dictionary', body: 'Only real English words score points.' },
  { heading: 'No duplicates', body: 'Each word can only be submitted once per game.' },
  { heading: 'Scoring', body: '3–4 letters = 1 pt · 5 = 2 pts · 6 = 3 pts · 7 = 5 pts · 8+ = 11 pts' },
];

const REASON_LABEL = {
  TOO_SHORT:            'too short (min 3 letters)',
  NOT_IN_DICTIONARY:    'not a word',
  NOT_ON_BOARD:         'not on board',
  DUPLICATE:            'already found',
  EMPTY_WORD:           'no word entered',
  GAME_NOT_FOUND:       'game not found',
  PLAYER_NOT_FOUND:     'player not found',
  PLAYER_NOT_IN_GAME:   'player not in this game',
  GAME_NOT_IN_PROGRESS: 'game is not active',
  OK:                   '✓',
  DEV_MODE:             '',
  ERROR:                'network error',
};

function formatTime(totalSeconds = 0) {
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
}

// -----------------------------------------------------------------------

export default function GamePage() {
  const location   = useLocation();
  const navigate   = useNavigate();
  const playerName = location.state?.playerName ?? 'Guest';
  const gameId     = location.state?.gameId   ?? null;
  const playerId   = location.state?.playerId ?? null;

  const [letters, setLetters]           = useState(location.state?.letters ?? DEV_PLACEHOLDER);
  const [boardLoading, setBoardLoading] = useState(() => gameId != null);
  const [score, setScore]               = useState(location.state?.score ?? 0);
  const [selectedPath, setSelectedPath] = useState([]);
  const [feedback, setFeedback]         = useState(null);
  const [foundWords, setFoundWords]     = useState([]);
  const [gameStatus, setGameStatus]     = useState('IN_PROGRESS');
  const [remainingTime, setRemainingTime] = useState(180);
  const [showRules, setShowRules]       = useState(false);
  const isGameOver = gameStatus === 'FINISHED' || remainingTime <= 0;

  const isDraggingRef = useRef(false);
  const pathRef       = useRef([]);
  const lettersRef    = useRef(letters);

  useEffect(() => {
    lettersRef.current = letters;
  }, [letters]);

  const updatePath = (newPath) => {
    pathRef.current = newPath;
    setSelectedPath([...newPath]);
  };

  // ---- Found Word Fetching Logic ---------------------------------------

  const fetchFoundWords = useCallback(async () => {
    if (gameId == null || playerId == null) return;
    try {
      const res = await fetch(`/api/game/${gameId}/player/${playerId}/words`);
      if (res.ok) {
        const data = await res.json();
        setFoundWords(data);
      }
    } catch (err) {
      console.error("Error fetching found words:", err);
    }
  }, [gameId, playerId]);

  /** * Poll for new words. Wrapping the call in a function inside the effect
   * satisfies the linter by avoiding synchronous setState calls during render.
   */
  useEffect(() => {
    const refreshBoard = () => {
      fetchFoundWords();
    };

    refreshBoard(); // Initial fetch
    const interval = setInterval(refreshBoard, 1000);

    return () => clearInterval(interval);
  }, [fetchFoundWords]);

useEffect(() => {
  if (gameId == null) return;

  const fetchGameState = async () => {
    try {
      const res = await fetch(`/api/game/${gameId}`);
      if (!res.ok) return;

      const data = await res.json();
      setGameStatus(data.status ?? 'IN_PROGRESS');

      if (typeof data.remainingSeconds === 'number') {
        setRemainingTime(data.remainingSeconds);
      }
    } catch (err) {
      console.error('Error fetching game state:', err);
    }
  };

  fetchGameState();
  const interval = setInterval(fetchGameState, 1000);

  return () => clearInterval(interval);
}, [gameId]);

  // ---- Drag logic -------------------------------------------------------

  const enterTile = useCallback((index) => {
    if (!isDraggingRef.current || index < 0) return;

    const path = pathRef.current;
    const existingIdx = path.indexOf(index);

    if (existingIdx !== -1) {
      updatePath(path.slice(0, existingIdx + 1));
      return;
    }

    if (isAdjacent(path[path.length - 1], index)) {
      updatePath([...path, index]);
    }
  }, []);

  const startDrag = useCallback((index) => {
    if (isGameOver) return;
    isDraggingRef.current = true;
    setFeedback(null);
    updatePath([index]);
  }, [isGameOver]);

  // ---- Word submission --------------------------------------------------

  const submitWord = useCallback(async (word) => {
    if (isGameOver) {
      setFeedback({ word, accepted: false, reason: 'GAME_NOT_IN_PROGRESS' });
      return;
    }

    if (word.length < MIN_WORD_LENGTH) {
      setFeedback({ word, accepted: false, reason: 'TOO_SHORT' });
      return;
    }

    if (gameId != null && playerId != null) {
      try {
        const res = await fetch(`/api/game/${gameId}/submit-word`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ playerId, word }),
        });

        const data = await res.json();

        setFeedback({ word, accepted: data.accepted, reason: data.reason });

        if (data.reason === 'PLAYER_NOT_FOUND') {
          const storedUser = JSON.parse(localStorage.getItem('bbUser') || 'null');
          if (storedUser?.isGuest) {
            clearExpiredSession(navigate);
            return;
          }
        }

        if (data.accepted) {
          if (data.points) setScore(s => s + data.points);
          fetchFoundWords();
        } else if (data.reason === 'GAME_NOT_IN_PROGRESS') {
          setGameStatus('FINISHED');
          setRemainingTime(0);
        }

      } catch {
        setFeedback({ word, accepted: false, reason: 'ERROR' });
      }
    } else {
      setFeedback({ word, accepted: null, reason: 'DEV_MODE' });
    }
  }, [gameId, playerId, fetchFoundWords, isGameOver, navigate]);

  const finalize = useCallback(() => {
    if (!isDraggingRef.current) return;
    isDraggingRef.current = false;

    const word = pathRef.current.map(i => lettersRef.current[i]).join('');
    updatePath([]);
    submitWord(word);
  }, [submitWord]);

  // ---- Board fetch ------------------------------------------------------

  useEffect(() => {
    if (gameId == null) return;
    const controller = new AbortController();

    fetch(`/api/game/${gameId}/board`, { signal: controller.signal })
        .then(res => res.json())
        .then(data => {
          const parsed = data.boardString.split('\n').flatMap(row => [...row]);
          if (parsed.length === 16) setLetters(parsed);
        })
        .catch(() => {})
        .finally(() => setBoardLoading(false));

    return () => controller.abort();
  }, [gameId]);

  useEffect(() => {
    window.addEventListener('mouseup', finalize);
    return () => window.removeEventListener('mouseup', finalize);
  }, [finalize]);

  // ---- Render -----------------------------------------------------------

  const currentWord = selectedPath.map(i => letters[i]).join('');

  let feedbackMod = '';
  if (feedback) {
    feedbackMod = feedback.accepted === true  ? 'ok'
        : feedback.accepted === false ? 'bad'
            : 'dev';
  }

  return (
    <>
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
          <div className="score-section">
            <span className="score-label">Time Left</span>
            <span className="score-value">{formatTime(remainingTime)}</span>
          </div>

          {isGameOver && (
            <div className="word-feedback word-feedback--bad">
              Time’s up! Round over.
            </div>
          )}

          <button className="rules-btn" onClick={() => setShowRules(true)}>? Rules</button>
          <button className="rules-btn" onClick={() => navigate('/home')}>⌂ Home</button>

          <div className="found-words-container">
            <div className="found-words-list">
              {foundWords.length > 0 ? (
                  foundWords.map((fw, idx) => (
                      <div key={`${fw.word}-${idx}`} className="found-word-entry">
                        <span className="found-word-text">{fw.word}</span>
                        <span className="found-word-pts">+{fw.points}</span>
                      </div>
                  ))
              ) : (
                  <p className="empty-list-text">Keep searching!</p>
              )}
            </div>
          </div>
        </aside>

        <main className="game-main">
          <div className="word-preview">
            {currentWord
                ? currentWord
                : feedback
                    ? (
                        <span className={`word-feedback word-feedback--${feedbackMod}`}>
                  {feedback.word}
                          {feedback.reason in REASON_LABEL && feedback.reason !== 'DEV_MODE'
                              ? ` — ${REASON_LABEL[feedback.reason]}`
                              : ''}
                </span>
                    )
                    : '\u00A0'}
          </div>

          <div className={`boggle-grid${boardLoading || isGameOver ? ' boggle-grid--loading' : ''}`}>            {letters.map((letter, i) => {
              const isSelected = selectedPath.includes(i);
              const isFirst    = selectedPath[0] === i;
              const classes    = [
                'tile',
                isSelected ? 'tile--selected' : '',
                isFirst    ? 'tile--first'    : '',
              ].filter(Boolean).join(' ');

              return (
                  <div
                      key={i}
                      className={classes}
                      onMouseDown={() => !isGameOver && startDrag(i)}
                      onMouseEnter={() => !isGameOver && enterTile(i)}
                  >
                <span className={`tile-letter${letter === 'Qu' ? ' tile-letter--qu' : ''}`}>
                  {letter}
                </span>
                  </div>
              );
            })}
          </div>
        </main>
      </div>

      {showRules && (
        <div className="rules-overlay" onClick={() => setShowRules(false)}>
          <div className="rules-modal" onClick={e => e.stopPropagation()}>
            <div className="rules-modal-header">
              <h2 className="rules-modal-title">Rules</h2>
              <button className="rules-modal-close" onClick={() => setShowRules(false)}>✕</button>
            </div>
            <ol className="rules-modal-list">
              {RULES.map((rule, i) => (
                <li key={i} className="rules-modal-item">
                  <span className="rules-modal-heading">{rule.heading}</span>
                  <span className="rules-modal-body">{rule.body}</span>
                </li>
              ))}
            </ol>
          </div>
        </div>
      )}
    </>
  );
}