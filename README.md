# Specification Document

## Boggle Buddies

### Project Abstract

**Boggle Buddies** is a real time multiplayer word game insired by the classic game **Boggle**, where players compete to find as many valid words as possible from a randomly generated letter board within a time limit. The application features both <u>live multiplayer matches</u> and a <u>player vs computer mode</u>. Users are able to play as a *guest* or *create an account* to track long term stats like wins and losses. The system includes *board shuffling algorithms*, *dictionary based words*, *rule enforcement* like no duplicate words, and a *dynamic scoring screen* that highlights unique vs shared words. This is built with a **React** frontend and a **Java Spring Boot** backend that allows *settings customization*, *real time game sync*, and *persistent data storage* using a **SQL** database and is containerized using **Docker** for consistency. 

### Customer

The general customer for **Boggle Buddies** includes casual word game players, students, and competitive players who enjoy fast paced vocab challenges, specifically those interested in a real time multiplayer setting. The system is designed for users who value social gameplay, competitive scoring, and customizable setting in an accessible web environment.

### Specification

#### Technology Stack

```mermaid
flowchart RL
subgraph Frontend
	A(Javascript: React)
end
	
subgraph Backend
	B(Java: Spring Boot REST API)
    R(Real-Time Layer: WebSocket / TBD)
end
	
subgraph Database
	C[(SQL Database)]
end

A <-->|"REST API (HTTP)"| B
B <-->|WebSocket| C
B <--> C
R <--> B
```

#### Database

```mermaid
erDiagram
    User ||--o{ Game : participates_in
    Game ||--o{ WordEntry : contains
    User ||--o{ WordEntry : submits
    Game ||--o{ Board : uses

    User {
        int user_id PK
        string username
        string password
        int wins
        int losses
        boolean is_guest
    }

    Game {
        int game_id PK
        string status
        string start_time
        string end_time
        int duration_seconds
    }

    Board {
        int board_id PK
        string grid
        string seed
    }

    WordEntry {
        int word_id PK
        int user_id FK
        int game_id FK
        string word
        int score
        boolean is_unique
    }
```

#### Class Diagram

```mermaid
classDiagram
    class User {
        +int userId
        +String username
        +boolean isGuest
        +int wins
        +int losses
    }
    class Game {
        +int gameId
        +Board board
        +List players
        +GameStatus status
        +void startGame()
        +void endGame()
        +void calculateScores()
    }
    class Board {
        +char[][] grid
        +void shuffleDice()
        +boolean isValidPath(String word)
    }
    class WordValidator {
        +boolean existsInDictionary(String word)
        +boolean followsRules(String word, Board board)
    }
    class WordEntry {
        +String word
        +int score
        +boolean isUnique
    }
    User --> WordEntry
    Game --> WordEntry
    Game --> Board
    Game --> User
    Game --> WordValidator
```

#### Sequence Diagram

```mermaid
sequenceDiagram

participant Player1
participant Player2
participant Frontend
participant Backend
participant Database

Player1 ->> Frontend: Join Room
Frontend ->> Backend: Create/Join Game
Backend ->> Database: Create/Join Game in DB
Backend ->> Frontend: Game Created

Player2 ->> Frontend: Join Room
Frontend ->> Backend: Join Game
Backend ->>Frontend: Game State Sync

Frontend ->> Backend: Submit Word
Backend ->>Backend: Validate Word
Backend ->> Database: Store WordEntry
Backend ->> Frontend: Real time Update
```

### Standards & Conventions

<!--This is a link to a seperate coding conventions document / style guide-->
[Style Guide & Conventions](STYLE.md)

