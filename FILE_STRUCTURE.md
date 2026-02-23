# Backend File Structure (Project_14)

This structure keeps your existing feature folders (`buggle_board/`, `buggle_rules/`, `buggle_scores/`) and documents each feature using a `.md` file (no `.java` files listed in this write-up).

---

## Folder Tree

```text
project_14/
├─ src/
│  └─ main/
│     └─ java/
│        └─ com/
│           ├─ frontend/
│           └─ backend/
│              ├─ config/
│              │  
│              ├─ common/
│              │  
│              ├─ session/
│              │
│              ├─ buggle_board/
│              │
│              ├─ buggle_rules/
│              │
│              ├─ buggle_scores/
│              │
│              └─ game/
│
└─ db/

```
## Config Folder
Will store configuration of websocket as well as connect frontend with backend by communicating global signals. It defines the “global wiring” for how clients connect and how messages move through the system. This keeps real-time and cross-cutting setup in one place.

### potential files
1. WebSocket configuration
2. Backend setup for frontend communication

---

## Common Folder  
Will store shared backend conventions used across every feature folder. It standardizes how responses, errors, and naming are done so modules stay consistent. This prevents each folder from inventing its own formats.

### potential files  
1. Standard success/error response format (JSON shape examples)  
2. Status code guidelines (when to use 400 vs 404 vs 500)  
3. Shared definitions/terms (game, session, player, round)  
4. Shared validation norms (lowercase, trim, max word length, etc.)  

---

## Session Folder  
Will store session behavior for guests and (later) registered users. It supports the Walking Skeleton by proving persistence through creating and retrieving a session. This also defines what a session is allowed to do in the system.

### potential files  
1. Guest session create + fetch flow (Walking Skeleton steps)  
2. Session data model (fields like id, type, createdAt, expiresAt)  
3. Guest vs user rules (what persists, what resets, limitations)  
4. Session lifecycle (expiration, cleanup, reconnect behavior)  

---

## Game Folder  
Will store the multiplayer game lifecycle and server-side game state rules. It explains how rooms are created, joined, started, timed, and ended. This becomes the “source of truth” documentation for syncing state.

### potential files  
1. Room lifecycle (create/join/leave/start/end)  
2. Game state schema (players, board, submissions, phase, timer)  
3. Real-time event list + payloads (state updates broadcast to clients)  
4. Edge cases (disconnect/rejoin, late join rules, tie handling)  

---

## Buggle_Board Folder  
Will store board generation behavior and the board format returned to clients. It documents how randomness is handled and what options exist for board size/settings. This keeps board creation independent from word validation.

### potential files  
1. Board generation method (dice shuffle vs random letters)  
2. Board options (size, seed/replay support, special tiles like “Qu”)  
3. Board response format (2D grid structure + metadata)  
4. Constraints (letter distribution, reproducibility rules)  

---

## Buggle_Rules Folder  
Will store word validation and rule checking documentation. It defines exactly what makes a word valid and what checks happen in what order. This is the main reference for gameplay correctness.

### potential files  
1. Dictionary strategy (file-based or DB-based lookup)  
2. Rule checks (length ≥ 3, adjacency, no reuse of same cell)  
3. Duplicate submission rules (per player, per round)  
4. Validation response format (valid/invalid + reason codes)  

---

## Buggle_Scores Folder  
Will store scoring rules and how end-of-game results are calculated. It documents how total scores are computed and how unique vs shared words are highlighted. This drives the final scoreboard screen.

### potential files  
1. Scoring rules (word length → points table)  
2. Unique vs shared word logic (compare lists across players)  
3. Scoreboard output format (totals, word lists, winner/tie)  
4. End-game summary rules (ties, empty submissions, penalties if any)  



