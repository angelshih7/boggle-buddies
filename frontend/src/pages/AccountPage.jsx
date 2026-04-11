import { useNavigate } from 'react-router-dom';
import './AccountPage.css';

const STORAGE_KEY = 'bbUser';

const INFO_ROWS = [
    { label: 'Username', key: 'username', icon: '👤' },
    { label: 'Email',    key: 'email',    icon: '✉️'  },
    { label: 'Player ID', key: 'id',      icon: '🔑'  },
];

export default function AccountPage() {
    const navigate = useNavigate();

    const storedUser = localStorage.getItem(STORAGE_KEY);
    const user       = storedUser ? JSON.parse(storedUser) : null;
    const playerName = user?.username ?? 'Guest';

    function handleSignOut() {
        localStorage.removeItem(STORAGE_KEY);
        navigate('/login');
    }

    return (
        <div className="account-page">
            <button className="account-back-btn" onClick={() => navigate('/home')}>
                ← Back
            </button>

            <div className="account-avatar">{playerName.charAt(0).toUpperCase()}</div>
            <h1 className="account-username">{playerName}</h1>
            <p className="account-subtitle">Account Details</p>

            <div className="account-card">
                {INFO_ROWS.map(({ label, key, icon }) => (
                    <div key={key} className="account-row">
                        <span className="account-row-icon">{icon}</span>
                        <div className="account-row-text">
                            <span className="account-row-label">{label}</span>
                            <span className="account-row-value">{user?.[key] ?? '—'}</span>
                        </div>
                    </div>
                ))}
            </div>

            <button className="account-signout-btn" onClick={handleSignOut}>
                Sign Out
            </button>
        </div>
    );
}
