# Frontend API — Users

## Quick links: How to make HTTP requests
- Fetch (MDN): https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
- Axios: https://axios-http.com/docs/intro

---

## Base URL
`/api/users`

---

## Error response format
```json
{
  "timestamp": "2026-03-06T12:34:56.789+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "username is required",
  "path": "/api/users/register"
}
```

---

## Register

### 1) API address call
`POST /api/users/register`

### 2) Depends on data created first
None.

### 3) Examples

#### Example request
```http
POST /api/users/register
Content-Type: application/json
```

```json
{
  "username": "diego",
  "email": "diego@example.com",
  "password": "secret"
}
```

### 4) Successful response

#### Status
`201 Created`

#### Body
```json
{
  "id": 12,
  "username": "diego",
  "email": "diego@example.com"
}
```

### 5) Failures

#### `400 Bad Request`
- `"Body is required"`
- `"username is required"`
- `"email is required"`
- `"password is required"`

#### `409 Conflict`
- `"Username already taken."`
- `"Email Already Taken"`
- `"Username or email already taken"`

---

## Login

### 1) API address call
`POST /api/users/login`

### 2) Depends on data created first
- A user account must already exist for the provided `username` (created via **Register**).

### 3) Examples

#### Example request
```http
POST /api/users/login
Content-Type: application/json
```

```json
{
  "username": "diego",
  "password": "secret"
}
```

### 4) Successful response

#### Status
`200 OK`

#### Body
```json
{
  "id": 12,
  "username": "diego",
  "email": "diego@example.com"
}
```

### 5) Failures

#### `400 Bad Request`
- `"Body is required"`
- `"username is required"`
- `"password is required"`

#### `401 Unauthorized`
- `"Invalid username or password"`

---

## Guest

### 1) API address call
`POST /api/users/guest`

### 2) Depends on data created first
None.

### 3) Examples

#### Example request (auto-generate username)
```http
POST /api/users/guest
Content-Type: application/json
```

```json
{ "username": "" }
```

#### Example request (preferred username)
```json
{ "username": "GuestDiego" }
```

### 4) Successful response

#### Status
`201 Created`

#### Body
```json
{
  "id": 99,
  "username": "GuestDiego-ABCD",
  "email": "guest_550e8400-e29b-41d4-a716-446655440000@guest.local"
}
```

### 5) Failures

#### `400 Bad Request`
- `"Body is required"` (only if backend enforces a body)

#### `409 Conflict`
- `"Username or email already taken"` (rare concurrency collision)

---

## Date-sensitive data (frontend caution)
- Guest accounts may be deleted after a TTL based on `users.created_at` (if cleanup is enabled). Frontend should treat guest identities as temporary.
