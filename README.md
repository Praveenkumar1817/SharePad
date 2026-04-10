# SharePad

A real-time collaborative note-taking application that lets multiple users view and edit shared notes simultaneously. Notes are accessed by a unique name, and users can optionally lock a note to prevent concurrent edits.

## Features

- **Real-time collaboration** – Multiple users can edit the same note at the same time using WebSocket (STOMP over SockJS).
- **Named notes** – Any note is reachable by its unique key (e.g. `/#my-cool-note`). Notes are created automatically on first access.
- **Google OAuth2 authentication** – Sign in with a Google account to unlock write and lock features.
- **Note locking** – An authenticated user can lock a note for 30 minutes to gain exclusive edit rights. The lock can be extended in 15-minute increments up to a maximum of 120 minutes and released early at any time.
- **Export** – Download a note as plain text (`.txt`), Markdown (`.md`), or PDF.
- **Auto-cleanup** – Expired locks are automatically removed every minute.

## Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2.3 |
| Persistence | Spring Data JPA, PostgreSQL |
| Authentication | Spring Security, OAuth2 (Google) |
| Real-time | Spring WebSocket, STOMP, SockJS |
| Frontend | Vanilla HTML / CSS / JavaScript |
| Containerisation | Docker (multi-stage build) |
| Deployment | Render.com |

## Project Structure

```
SharePad/
├── Dockerfile                  # Multi-stage Docker build
├── render.yaml                 # Render.com deployment config
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/sharepad/
│       │   ├── SharePadApplication.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java   # Spring Security & OAuth2 setup
│       │   │   └── WebSocketConfig.java  # STOMP broker configuration
│       │   ├── controller/
│       │   │   ├── AuthController.java   # GET /api/auth/me
│       │   │   ├── ExportController.java # GET /api/export/{noteKey}?format=
│       │   │   ├── LockController.java   # POST /api/lock/{noteKey}[/extend|/unlock]
│       │   │   └── NoteController.java   # GET /api/notes/{noteKey}
│       │   ├── dto/
│       │   │   ├── LoginResponse.java
│       │   │   └── NoteResponse.java
│       │   ├── model/
│       │   │   ├── Note.java
│       │   │   ├── NoteLock.java
│       │   │   └── User.java
│       │   ├── repository/
│       │   │   ├── NoteLockRepository.java
│       │   │   ├── NoteRepository.java
│       │   │   └── UserRepository.java
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   ├── ExportService.java
│       │   │   ├── LockService.java
│       │   │   └── NoteService.java
│       │   └── websocket/
│       │       ├── EditMessage.java
│       │       └── NoteWebSocketController.java
│       └── resources/
│           ├── application.yml
│           └── schema.sql
└── frontend/
    ├── index.html
    ├── css/
    │   └── style.css
    └── js/
        ├── api.js          # REST API calls
        ├── auth.js         # Authentication helpers
        ├── editor.js       # Main editor logic & UI state
        ├── lockTimer.js    # Countdown timer for active lock
        └── websocket.js    # STOMP/SockJS client
```

## Database Schema

```sql
-- OAuth2 users
CREATE TABLE users (
    id           VARCHAR(255) PRIMARY KEY,
    email        VARCHAR(255) UNIQUE NOT NULL,
    name         VARCHAR(255) NOT NULL,
    provider     VARCHAR(50)  NOT NULL,
    provider_id  VARCHAR(255) NOT NULL
);

-- Notes
CREATE TABLE notes (
    id         BIGSERIAL PRIMARY KEY,
    note_key   VARCHAR(255) UNIQUE NOT NULL,
    content    TEXT,
    owner_id   VARCHAR(255) REFERENCES users(id),
    expires_at TIMESTAMP
);

-- Active locks
CREATE TABLE note_locks (
    id                BIGSERIAL PRIMARY KEY,
    note_id           BIGINT REFERENCES notes(id) ON DELETE CASCADE,
    locked_by         VARCHAR(255) REFERENCES users(id),
    locked_until      TIMESTAMP NOT NULL,
    total_lock_minutes INT DEFAULT 0
);
```

## REST API

| Method | Endpoint | Auth required | Description |
|--------|----------|:-------------:|-------------|
| `GET` | `/api/auth/me` | No | Returns current user info and login URL |
| `GET` | `/api/notes/{noteKey}` | No | Fetch or create a note |
| `GET` | `/api/export/{noteKey}?format={txt\|md\|pdf}` | No | Download note in the requested format |
| `POST` | `/api/lock/{noteKey}` | Yes | Lock the note for 30 minutes |
| `POST` | `/api/lock/{noteKey}/extend` | Yes | Extend an existing lock by 15 minutes (max 120 min total) |
| `POST` | `/api/lock/{noteKey}/unlock` | Yes | Release the lock early |
| `POST` | `/logout` | Yes | Sign out |

### WebSocket

| Endpoint | Type | Description |
|----------|------|-------------|
| `/ws` | SockJS handshake | WebSocket connection entry point |
| `/app/note/{noteKey}/edit` | STOMP send | Publish a content update |
| `/topic/note/{noteKey}` | STOMP subscribe | Receive content updates from other users |

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL 14+
- A Google OAuth2 application (Client ID and Secret)

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/Praveenkumar1817/SharePad.git
   cd SharePad
   ```

2. **Configure the database**

   Create a local PostgreSQL database named `sharepad` and a user with access to it, or adjust the defaults in `backend/src/main/resources/application.yml`.

3. **Set environment variables**

   The application reads the following environment variables (defaults are shown in brackets):

   | Variable | Default | Description |
   |---|---|---|
   | `DB_HOST` | `localhost` | PostgreSQL host |
   | `DB_PORT` | `5432` | PostgreSQL port |
   | `DB_NAME` | `sharepad` | Database name |
   | `DB_USER` | `postgres` | Database user |
   | `DB_PASS` | `postgres` | Database password |
   | `GOOGLE_CLIENT_ID` | – | Google OAuth2 client ID |
   | `GOOGLE_CLIENT_SECRET` | – | Google OAuth2 client secret |
   | `FRONTEND_URL` | `http://localhost:5500` | Allowed CORS origin |

   Export them in your shell or add them to an `.env` file sourced before running the app.

4. **Build and run the backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The server starts on port `8080`. The frontend is served as static content at `http://localhost:8080`.

5. **Optional – serve the frontend separately**

   Open `frontend/index.html` with a local HTTP server (e.g. VS Code Live Server on port 5500). Make sure `FRONTEND_URL` is set accordingly so CORS is allowed.

### Docker

Build and run the entire application in one container:

```bash
docker build -t sharepad .
docker run -p 8080:8080 \
  -e DB_HOST=<host> \
  -e DB_PORT=5432 \
  -e DB_NAME=sharepad \
  -e DB_USER=<user> \
  -e DB_PASS=<password> \
  -e GOOGLE_CLIENT_ID=<client_id> \
  -e GOOGLE_CLIENT_SECRET=<client_secret> \
  sharepad
```

The application will be available at `http://localhost:8080`.

## Deployment on Render

The repository includes a `render.yaml` Blueprint that provisions:

- A **Web Service** running the Docker image
- A managed **PostgreSQL** database (`sharepad-db`)

Steps:

1. Fork or push this repository to GitHub.
2. In the Render dashboard, create a new **Blueprint** and connect the repository.
3. Set the two secrets that are not synced automatically:
   - `GOOGLE_CLIENT_ID`
   - `GOOGLE_CLIENT_SECRET`
4. Update `FRONTEND_URL` in `render.yaml` (or the Render dashboard) to the URL assigned to your web service.
5. Add the Render callback URL to your Google OAuth2 application's **Authorised redirect URIs**:
   ```
   https://<your-service>.onrender.com/login/oauth2/code/google
   ```

## Google OAuth2 Setup

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create (or select) a project and navigate to **APIs & Services → Credentials**.
3. Create an **OAuth 2.0 Client ID** of type *Web application*.
4. Add the following **Authorised redirect URIs**:
   - `http://localhost:8080/login/oauth2/code/google` (for local development)
   - `https://<your-render-service>.onrender.com/login/oauth2/code/google` (for production)
5. Copy the **Client ID** and **Client Secret** into the corresponding environment variables.

## How It Works

1. A user visits the app and enters a note name (or follows a direct link like `/#my-note`).
2. The frontend calls `GET /api/notes/{noteKey}` to load or create the note and fetch its current lock state.
3. A WebSocket connection is opened to `/ws`. The client subscribes to `/topic/note/{noteKey}` to receive live updates.
4. Every keystroke is sent over STOMP to `/app/note/{noteKey}/edit`. The server broadcasts the new content to all other subscribers.
5. An authenticated user can lock the note. While locked, only the lock owner can edit; other users see the editor in read-only mode and a "Locked by …" badge.
6. Locks expire automatically after the configured duration and are cleaned up server-side every 60 seconds.
