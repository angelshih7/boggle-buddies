# Boggle Buddies 

Real-time multiplayer word game. React frontend, Java Spring Boot + MySQL backend, containerized with Docker, built by a 6-person Agile team for CS/ECE 506 at UW-Madison (Spring 2026).

## Features
- Live multiplayer matches with shareable game codes
- Guest play or registered accounts with persistent win/loss stats
- Dictionary-based word validation and duplicate-word prevention
- Dynamic scoring with unique vs shared word highlighting
- Customizable game settings

## My contributions (Angel Shih)
- Built the game timer end to end: countdown and auto-end logic in Spring Boot, timer API, and the React countdown UI
- Implemented duplicate-word prevention and fixed scoring and winner-selection logic
- Added the frontend test job to our GitLab CI/CD pipeline
- Wrote unit tests for word validation, scoring, board generation, and shuffling
- Built the game code display with copy-to-clipboard for joining games

Built with five great teammates who own the rest of the commit history.

## Docs
- [Full specification with architecture diagrams](SPECIFICATION.md)
- [Style guide and conventions](STYLE.md)

## Running locally
Requires Docker.

    docker compose up

Then open http://localhost:8080