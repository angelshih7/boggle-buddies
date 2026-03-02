import { useState } from "react";
import { Card, Form, Button } from "react-bootstrap";
import { useNavigate, Link } from "react-router-dom"
import "./LoginPage.css";

const USERNAME_REGEX = /^[a-zA-Z0-9_]*$/;
const MIN_USERNAME_LENGTH = 5;
const MIN_PASSWORD_LENGTH = 7;
export default function SignupPage() {
    const [usernameInput, setUsernameInput] = useState("");
    const [passwordInput, setPasswordInput] = useState("");
    const [confirmPasswordInput, setConfirmPasswordInput] = useState("");
    const [usernameError, setUsernameError] = useState(false);
    const navigate = useNavigate();

    function handleSignup(e) {
        e.preventDefault();
        const userName = usernameInput.trim();
        const password = passwordInput.trim();
        const confirmPassword = confirmPasswordInput.trim();

        // Basic requirements for username and password
        if (!userName || !password || !confirmPassword) {
            alert("Please fill out all fields.");
            return;
        }
        if (userName.length < MIN_USERNAME_LENGTH) {
            alert(`Username should be at least ${MIN_USERNAME_LENGTH} characters.`);
            return;
        }
        if (password !== confirmPassword){
            alert("Passwords do not match!");
            return;
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            alert(`Password should be at least ${MIN_PASSWORD_LENGTH} characters.`);
            return;
        }

        // TODO: Call API to check signin credentials. If valid, logs in

        alert("Account created!");
        navigate("/home");
    }

    return (
        <div className="login-wrap">
            <img 
                src="logo.png"
                alt="An image of the Boggle Buddies logo" 
                style={{ width: 300 }}
            />
            <h1 className="login-title">Create your account</h1>
            <Card className="login-card">
                <Form onSubmit={handleSignup}>
                    <Form.Group>
                        <Form.Label>Username:</Form.Label>
                        <Form.Control 
                            className="bb-input"
                            value={usernameInput} 
                            onChange={e => {
                                const value = e.target.value;
                                setUsernameInput(value);
                                setUsernameError(!USERNAME_REGEX.test(value))
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
                    <Form.Group>
                        <Form.Label>Confirm Password:</Form.Label>
                        <Form.Control
                            className="bb-input"
                            value={confirmPasswordInput} 
                            onChange={e=>setConfirmPasswordInput(e.target.value)}
                            placeholder="Confirm password"
                            type="password"
                        />
                    </Form.Group>
                    <Button 
                        className="btn-primary" 
                        disabled={!usernameInput || !passwordInput || !confirmPasswordInput || usernameError}
                        type="submit"
                    >Create Account</Button>
                </Form>
                <div>
                    Already have an account?{" "}
                    <Link to="/login">Log in</Link>
                </div>
            </Card>
        </div>
    );
}