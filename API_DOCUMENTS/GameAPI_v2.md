# Frontend API — Games

## Quick links: How to make HTTP requests
- Fetch (MDN): https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
- Axios: https://axios-http.com/docs/intro

---

## Base URL
`/api/game`

---

## Error response format
```json
{
  "timestamp": "2026-03-06T12:34:56.789+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Game id not found.",
  "path": "/api/game/123"
}
```

---

## Create game

### 1) API address call
`POST /api/game`

### 2) Depends on data created first
- **SOLO / BOT**: requires a valid `playerId` (obtain via Users API: register/login/guest).
- **MULTIPLAYER**: requires a valid `sessionCode` and:
  - session exists for `sessionCode`
  - session contains **exactly 2 usernames**
  - both usernames exist in `users` (account or guest created first)

### 3) Examples

#### Body schema
```json
{
  "mode": "SOLO | BOT | MULTIPLAYER",
  "playerId": 12,
  "sessionCode": "ABCD"
}
```

#### SOLO example request
```http
POST /api/game
Content-Type: application/json
```

```json
{
  "mode": "SOLO",
  "playerId": 12
}
```

#### BOT example request
```json
{
  "mode": "BOT",
  "playerId": 12
}
```

#### MULTIPLAYER example request
```json
{
  "mode": "MULTIPLAYER",
  "sessionCode": "ABCD"
}
```

### 4) Successful response

#### Status
`201 Created`

#### Body
```json
{
  "gameId": 50,
  "player1Id": 12,
  "player2Id": 34,
  "boardId": "0df1b1b4-9b0e-4f53-bb5a-2c84f2a4a4e7",
  "status": "IN_PROGRESS"
}
```

### 5) Failures

#### `400 Bad Request`
- `"Body is required"`
- `"playerId is required"` (SOLO)
- `"PlayerId is required"` (BOT)
- `"sessionCode is required"` (MULTIPLAYER)
- `"Unknown mode"`

#### `404 Not Found`
- `"playerId not found"` / `"PlayerId not found"`
- `"session not found"`
- `"User not found: <username>"`

#### `409 Conflict`
- `"Need 2 Players in session"`
- `"More than 2 Players not supported"`

---

## Get game summary

### 1) API address call
`GET /api/game/{gameId}`

### 2) Depends on data created first
- A game must already exist, and you must have a valid `gameId` (typically returned from **Create game**).

### 3) Examples

#### Example request
```http
GET /api/game/50
```

### 4) Successful response

#### Status
`200 OK`

#### Body
```json
{
  "gameId": 50,
  "player1Id": 12,
  "player2Id": 34,
  "boardId": "0df1b1b4-9b0e-4f53-bb5a-2c84f2a4a4e7",
  "status": "IN_PROGRESS"
}
```

### 5) Failures

#### `404 Not Found`
- `"Game id not found."`

---

## Get board for game

### 1) API address call
`GET /api/game/{gameId}/board`

### 2) Depends on data created first
- A game must already exist, and you must have a valid `gameId` (typically returned from **Create game**).

### 3) Examples

#### Example request
```http
GET /api/game/50/board
```

### 4) Successful response

#### Status
`200 OK`

#### Body
```json
{
  "boardId": "0df1b1b4-9b0e-4f53-bb5a-2c84f2a4a4e7",
  "boardString": "ABCDEFGHIJKLMNOP"
}
```

### 5) Failures

#### `404 Not Found`
- `"board related to game not found"`

---

## Create sample board (no game)

### 1) API address call
`POST /api/game/board`

### 2) Depends on data created first
None.

### 3) Examples

#### Example request
```http
POST /api/game/board
```

### 4) Successful response

#### Status
`200 OK`

#### Body
```json
{
  "boardId": "f2b1c9d3-2a3b-4b77-a9a1-9c08d0b5efc2",
  "boardString": "ABCDEFGHIJKLMNOP"
}
```

### 5) Failures

#### `500 Internal Server Error`
- `"Failed to create board"` (if persistence fails)

---

## Date-sensitive data (frontend caution)
- The backend sets `startedAt = LocalDateTime.now()` when creating a game. This timestamp is server-generated and not timezone-aware. If timestamps are later exposed to the frontend, convert/display carefully.
