import { useState } from "react";
import { Card, Form, Button } from "react-bootstrap";
import { useNavigate, Link } from "react-router-dom"
import "./LoginPage.css";

const USERNAME_REGEX = /^[a-zA-Z0-9_]*$/;
const MIN_USERNAME_LENGTH = 5;
const MIN_PASSWORD_LENGTH = 7;
export default function LoginPage() {
    const [usernameInput, setUsernameInput] = useState("");
    const [passwordInput, setPasswordInput] = useState("");
    const [usernameError, setUsernameError] = useState(false);
    const navigate = useNavigate();

    // Basic handler for logging in that will call API after checking basic requirements
    function handleLogin(e) {
        e.preventDefault();
        const userName = usernameInput.trim();
        const password = passwordInput.trim();

        // Basic requirements for username and password
        if (!userName || !password) {
            alert("Please enter both a username and a password.");
            return;
        }
        if (userName.length < MIN_USERNAME_LENGTH) {
            alert(`Username should be at least ${MIN_USERNAME_LENGTH} characters.`);
            return;
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            alert(`Password should be at least ${MIN_PASSWORD_LENGTH} characters.`);
            return;
        }

        // TODO: Call API to check login credentials and set to logged in if OK

        alert("Logged in!");
        navigate("/home");
    }

    return (
        <div className="login-wrap">
            <img 
                src="logo.png"
                alt="An image of the Boggle Buddies logo" 
                style={{ width: 300 }}
            />
            <h1 className="login-title">Ready to play?</h1>
            <Card className="login-card">
                <Form onSubmit={handleLogin}>
                    <Form.Group>
                        <Form.Label>Username:</Form.Label>
                        <Form.Control 
                            className="bb-input"
                            value={usernameInput} 
                            onChange={e => {
                                const value = e.target.value;
                                setUsernameInput(value);
                                setUsernameError(!USERNAME_REGEX.test(value));
                            }}
                            placeholder="Enter your username"
                            isInvalid={usernameError}
                        />
                        {usernameError && (
                            <div className="bb-error">
                                Only letters, numbers, and underscores allowed.
                            </div>
                        )}
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Password:</Form.Label>
                        <Form.Control
                            className="bb-input"
                            value={passwordInput} 
                            onChange={e => setPasswordInput(e.target.value)}
                            placeholder="Enter your password"
                            type="password"
                        />
                    </Form.Group>
                    <Button 
                        className="btn-primary"
                        disabled={!usernameInput || !passwordInput || usernameError} 
                        type="submit"
                    >Log in</Button>
                </Form>
                <Button className="btn-secondary" onClick={() => navigate("/home")}>Play as guest</Button>
                <div>
                    New here?{" "}
                    <Link to="/signup">Create an account</Link>
                </div>
            </Card>
        </div>
    );
}