import { useNavigate } from 'react-router-dom';
import './WordsModal.css';

export default function WordsModal({ words }) {
  const navigate = useNavigate();

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <div id="header-style">
          <h1>Words in Game</h1>
        </div>
        <div className="modal-word-list">
          <ul>
            {words.map((word, idx) => (
              <li key={`${word}-${idx}`}>{word}</li>
            ))}
          </ul>
        </div>
        <div>
          <button className="modal-close-btn" onClick={() => navigate('/')}>
            Close and Continue
          </button>
        </div>
      </div>
    </div>
  );
}
