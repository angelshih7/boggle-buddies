import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './WordsModal.css';

export default function WordsModal({
  comparison,
  isMultiplayer = false,
  mySortedWords = [],
  opponentSortedWords = [],
  onClose = null,
}) {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('comparison');
  const handleClose = () => (onClose ? onClose() : navigate('/home'));

  return (
    <div className="modal-overlay">
      <div className="modal-box">

        <div id="header-style">
          <h1>Game Over</h1>
        </div>

        <div className="modal-tabs">
          <button
            className={`modal-tab${activeTab === 'comparison' ? ' modal-tab--active' : ''}`}
            onClick={() => setActiveTab('comparison')}
          >
            Comparison
          </button>
          {isMultiplayer && (
            <button
              className={`modal-tab${activeTab === 'vs-opponent' ? ' modal-tab--active' : ''}`}
              onClick={() => setActiveTab('vs-opponent')}
            >
              vs Opponent
            </button>
          )}
        </div>

        {activeTab === 'comparison' && (
          comparison ? (
            <div className="modal-comparison">
              <div className="modal-comparison-col">
                <h3 className="modal-comparison-title modal-comparison-title--found">
                  Found ({comparison.foundWords.length})
                </h3>
                <ul>
                  {comparison.foundWords.map((w, i) => (
                    <li key={i} className="modal-comparison-item--found">
                      <span className="modal-word-text">{w.word}</span>
                      <span className="modal-word-pts">+{w.points}</span>
                    </li>
                  ))}
                </ul>
              </div>
              <div className="modal-comparison-col">
                <h3 className="modal-comparison-title modal-comparison-title--missed">
                  Missed ({comparison.missedWords.length})
                </h3>
                <ul>
                  {comparison.missedWords.map((w, i) => (
                    <li key={i} className="modal-comparison-item--missed">
                      <span className="modal-word-text">{w.word}</span>
                      <span className="modal-word-pts">+{w.points}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          ) : (
            <p className="modal-empty">Loading&hellip;</p>
          )
        )}

        {activeTab === 'vs-opponent' && isMultiplayer && (
          <div className="modal-comparison">
            <div className="modal-comparison-col">
              <h3 className="modal-comparison-title modal-comparison-title--found">
                You ({mySortedWords.length})
              </h3>
              <ul>
                {mySortedWords.map((fw, i) => (
                  <li key={i} className="modal-comparison-item--found">
                    <span className="modal-word-text">{fw.word}</span>
                    <span className="modal-word-pts">+{fw.points}</span>
                  </li>
                ))}
              </ul>
            </div>
            <div className="modal-comparison-col">
              <h3 className="modal-comparison-title modal-comparison-title--missed">
                Opponent ({opponentSortedWords.length})
              </h3>
              <ul>
                {opponentSortedWords.map((fw, i) => (
                  <li key={i} className="modal-comparison-item--missed">
                    <span className="modal-word-text">{fw.word}</span>
                    <span className="modal-word-pts">+{fw.points}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}

        <button className="modal-close-btn" onClick={handleClose}>
          Close and Continue
        </button>

      </div>
    </div>
  );
}
