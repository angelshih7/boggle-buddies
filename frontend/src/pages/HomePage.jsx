import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './HomePage.css';

const STORAGE_KEY = 'bbUser';

export default function HomePage() {
    const storedUser  = localStorage.getItem(STORAGE_KEY);
    const user        = storedUser ? JSON.parse(storedUser) : null;
    const playerName  = user?.username ?? 'Guest';

    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    /**
     * Creates a solo game via POST /api/game, then navigates to GamePage.
     * GamePage will fetch the board itself using the returned gameId.
     */
    async function handlePlaySolo() {
        if (!user?.id) {
            alert('You must be logged in to start a game.');
            return;
        }

        setLoading(true);
        try {
            const res = await fetch('/api/game', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ mode: 'SOLO', playerId: user.id }),
            });

            if (!res.ok) {
                alert('Failed to create game. Please try again.');
                return;
            }

            const data = await res.json();
            navigate('/game', {
                state: {
                    playerName,
                    gameId:   data.gameId,
                    playerId: user.id,
                },
            });
        } catch {
            alert('Network error. Is the server running?');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="home-page">
            <div className="home-avatar">{playerName.charAt(0).toUpperCase()}</div>
            <h1 className="home-username">{playerName}</h1>

            <div className="home-buttons">
                <button
                    className="home-btn home-btn--primary"
                    onClick={handlePlaySolo}
                    disabled={loading}
                >
                    {loading ? 'Starting…' : '▶ Play Solo'}
                </button>

                <button className="home-btn home-btn--secondary" disabled>
                    🏆 Rank
                </button>

                <button className="home-btn home-btn--secondary" disabled>
                    👤 My Account
                </button>
            </div>
        </div>
    );
}
