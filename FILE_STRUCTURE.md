# Backend File Structure (Project_14)

This structure keeps your existing feature folders (`buggle_board/`, `buggle_rules/`, `buggle_scores/`) and documents each feature using a `.md` file (no `.java` files listed in this write-up).

---

## Folder Tree

```text
boggle-app/
├── Docker/
│   ├── docker-compose.yml
│   └── Dockerfile
├── README.md
├── frontend/                          # React app
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   │   ├── HomePage.jsx
│   │   │   ├── GamePage.jsx
│   │   │   └── LeaderboardPage.jsx
│   │   ├── services/                  # API call functions
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── Dockerfile
│   └── package.json
├── backend/                           # Spring Boot app
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── boggle/
│       │   │           ├── controller/
│       │   │           ├── service/
│       │   │           ├── model/            # Game, Board, Player Word
│       │   │           ├── repository/
│       │   │           └── BoggleApplication.java
│       │   └── resources/
│       │       ├── application.properties
│       │       └── schema.sql
│       └── test/
│           └── java/                         # JUnit/Mockito tests
└── database/
    └── init.sql

```

## Backend folders
## controller/
**Summary of Folder:** Contains REST endpoints that receive HTTP/WebSocket requests from the frontend and return responses. It should handle request/response mapping, input validation at the boundary, and delegate all business logic to the service layer. Controllers should remain thin to keep logic testable and maintainable.  
**Potential Files:**  
- `GameController.java`  
- `LobbyController.java`  
- `ScoreController.java`  

## service/
**Summary of Folder:** Implements the application’s business logic and orchestration for gameplay, scoring, and session flow. It coordinates between controllers, repositories, and any external integrations while enforcing game rules. Services should be reusable and independent of transport details like HTTP.  
**Potential Files:**  
- `GameService.java`  
- `BoardService.java`  
- `ScoringService.java`  
- `SessionService.java`  

## model/
**Summary of Folder:** Defines the core domain objects and data structures used throughout the application (e.g., Game, Board, Player, Word). It should contain entities, DTOs, and value objects that represent game state and requests/responses. Models should be kept consistent and predictable to prevent rule duplication across layers.  
**Potential Files:**  
- `Game.java`  
- `Board.java`  
- `Player.java`  
- `Word.java`  
- `GameStateDto.java`  

## repository/
**Summary of Folder:** Provides the data access layer for reading and writing persistent data (e.g., user stats, saved games, leaderboards). It should encapsulate database queries so services do not depend on SQL or storage details directly. Repositories commonly use Spring Data interfaces and query methods for clarity and consistency.  
**Potential Files:**  
- `PlayerRepository.java`  
- `GameRepository.java`  
- `LeaderboardRepository.java`   

## resources/
**Summary of Folder:** Stores runtime configuration and application resources that are packaged with the backend. It typically includes environment-specific settings, database initialization scripts, and configuration for Spring Boot. Resources should be treated as deployable assets and kept consistent across environments.  
**Potential Files:**  
- `application.properties`  
- `application-dev.properties`  
- `application-test.properties`  
- `schema.sql`  
- `data.sql`  

## test/
**Summary of Folder:** Contains automated tests that verify correctness of controllers, services, and repositories using JUnit and Mockito. It should include unit tests for business logic and integration tests for Spring context and persistence boundaries. Tests should be organized to mirror the main package structure for quick navigation.  
**Potential Files:**  
- `GameServiceTest.java`  
- `BoardServiceTest.java`  
- `GameControllerTest.java`  
- `RepositoryIntegrationTest.java`  
- `BoggleApplicationTests.java`  

### AI Statement
#### AI was used in collaboration to write file.
Prompt Used:
I need to make and File_structure.md summarizing some points.
"""
Context Debatable repo structure

├── backend/                           # Spring Boot app
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── boggle/
│       │   │           ├── controller/
│       │   │           ├── service/
│       │   │           ├── model/            # Game, Board, Player Word
│       │   │           ├── repository/
│       │   │           └── BoggleApplication.java
│       │   └── resources/
│       │       ├── application.properties
│       │       └── schema.sql
│       └── test/
│           └── java/                         # JUnit/Mockito tests

Instructions:
- Explain Controller, Service, Model, repository, BoggleApplication.java
- Explain resources 
- Explain Test

constraints:
1. Must use Mark Down
2. No Emojis
3. summary must be at most 3 sentences long

Structure of Explanation:

Summary of Folder: 3 sentence at most about what folder should hold.
Potential Files: at least 2 potential files (stop if 6 potential files)

Writing Style: Formal yet intuitive no overly complex language
"""
