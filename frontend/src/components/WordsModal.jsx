import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './WordsModal.css';

export default function WordsModal({ boardWords, comparison }) {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('board');

  return (
    <div className="modal-overlay">
      <div className="modal-box">

        <div id="header-style">
          <h1>Game Over</h1>
        </div>

        <div className="modal-tabs">
          <button
            className={`modal-tab${activeTab === 'board' ? ' modal-tab--active' : ''}`}
            onClick={() => setActiveTab('board')}
          >
            Words on Board ({boardWords.length})
          </button>
          <button
            className={`modal-tab${activeTab === 'comparison' ? ' modal-tab--active' : ''}`}
            onClick={() => setActiveTab('comparison')}
          >
            Comparison
          </button>
        </div>

        {activeTab === 'board' && (
          <div className="modal-word-list">
            {boardWords.length > 0 ? (
              <ul>
                {boardWords.map((item, idx) => (
                  <li key={`${item.word}-${idx}`}>
                    <span className="modal-word-text">{item.word}</span>
                    <span className="modal-word-pts">+{item.points}</span>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="modal-empty">No words found on board.</p>
            )}
          </div>
        )}

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
            <p className="modal-empty">Loading…</p>
          )
        )}

        <button className="modal-close-btn" onClick={() => navigate('/home')}>
          Close and Continue
        </button>

      </div>
    </div>
  );
}
