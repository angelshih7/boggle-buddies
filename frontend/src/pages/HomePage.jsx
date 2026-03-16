const STORAGE_KEY = "bbUser";

export default function HomePage() {
    // TODO: cookie?
    const storedUser = localStorage.getItem(STORAGE_KEY);
    const user = storedUser ? JSON.parse(storedUser) : null;

    return (
        <div>
            <h1>Home Page</h1>
            <p>Hello {user ? user.username : "Guest"}!</p>
        </div>
    );
}
