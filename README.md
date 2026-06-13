<div align="center">

# 📝 SharePad

**Real-time collaborative note-taking — open a note, share the link, start writing together.**

[![Live Demo](https://img.shields.io/badge/Live%20Demo-sharepad--nu.vercel.app-blue?style=for-the-badge&logo=vercel)](https://sharepad-nu.vercel.app)
[![Backend](https://img.shields.io/badge/Backend-Render-46E3B7?style=for-the-badge&logo=render)](https://sharepad-y829.onrender.com)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-4169E1?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

</div>

---

## ✨ Features

| Feature | Description |
|---|---|
| ⚡ **Real-time collaboration** | Multiple users edit the same note simultaneously via WebSocket (STOMP over SockJS) |
| 🔗 **Named notes** | Any note is reachable by its unique key — just visit `/#my-note`. Created automatically on first access |
| 🔐 **Google OAuth2** | Sign in with Google to unlock write, lock, and export features |
| 🔒 **Note locking** | Lock a note for 30 minutes for exclusive edit rights. Extend in 15-min increments (max 120 min) or release early |
| 📤 **Export** | Download any note as `.txt`, `.md`, or `.pdf` |
| 🧹 **Auto-cleanup** | Expired locks are purged automatically every 60 seconds |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────┐
│                   Browser (Client)                   │
│  HTML + CSS + Vanilla JS (served via Vercel)         │
│                                                      │
│   ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│   │  auth.js │  │  api.js  │  │  websocket.js    │  │
│   └────┬─────┘  └────┬─────┘  └────────┬─────────┘  │
└────────┼─────────────┼─────────────────┼────────────┘
         │  OAuth2     │  REST API        │  WebSocket
         ▼             ▼                 ▼
┌─────────────────────────────────────────────────────┐
│             Spring Boot Backend (Render)              │
│                                                      │
│  ┌──────────────┐  ┌─────────────┐  ┌────────────┐  │
│  │ SecurityConfig│  │  Controllers│  │  WS Broker │  │
│  │ (OAuth2/CORS) │  │  (REST API) │  │  (STOMP)   │  │
│  └──────────────┘  └──────┬──────┘  └─────┬──────┘  │
│                            │               │          │
│              ┌─────────────▼───────────────▼──────┐  │
│              │      Services (Business Logic)      │  │
│              │  NoteService │ LockService │ ...    │  │
│              └──────────────────────┬─────────────┘  │
└─────────────────────────────────────┼────────────────┘
                                      │ JPA
                                      ▼
                          ┌─────────────────────┐
                          │  PostgreSQL (Render) │
                          │  users / notes /     │
                          │  note_locks          │
                          └─────────────────────┘
```

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Frontend** | Vanilla HTML5 / CSS3 / JavaScript (ES6+) |
| **Backend** | Java 17, Spring Boot 3.2.3 |
| **Real-time** | Spring WebSocket, STOMP protocol, SockJS |
| **Persistence** | Spring Data JPA, PostgreSQL 14 |
| **Authentication** | Spring Security, OAuth2 (Google) |
| **Containerisation** | Docker (multi-stage build) |
| **Frontend Hosting** | Vercel |
| **Backend Hosting** | Render (Docker web service + managed PostgreSQL) |

---

## 📁 Project Structure

```
SharePad/
├── Dockerfile                  # Multi-stage Docker build
├── render.yaml                 # Render.com deployment blueprint
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/sharepad/
│       │   ├── SharePadApplication.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java      # Spring Security & CORS
│       │   │   └── WebSocketConfig.java     # STOMP broker config
│       │   ├── controller/
│       │   │   ├── AuthController.java      # GET /api/auth/me
│       │   │   ├── ExportController.java    # GET /api/export/{noteKey}
│       │   │   ├── LockController.java      # POST /api/lock/{noteKey}
│       │   │   └── NoteController.java      # GET /api/notes/{noteKey}
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
│           └── schema.sql              # Reference schema (managed by Hibernate)
└── frontend/
    ├── index.html
    ├── vercel.json                     # Vercel static site config
    ├── css/
    │   └── style.css
    └── js/
        ├── api.js          # REST API calls
        ├── auth.js         # Google OAuth2 helpers
        ├── editor.js       # Main editor logic & UI state
        ├── lockTimer.js    # Countdown timer for active lock
        └── websocket.js    # STOMP/SockJS client
```

---

## 🔌 REST API

| Method | Endpoint | Auth | Description |
|--------|----------|:----:|-------------|
| `GET` | `/api/auth/me` | ✗ | Returns current user info and Google login URL |
| `GET` | `/api/notes/{noteKey}` | ✗ | Fetch (or auto-create) a note by key |
| `GET` | `/api/export/{noteKey}?format={txt\|md\|pdf}` | ✗ | Download note in the requested format |
| `POST` | `/api/lock/{noteKey}` | ✓ | Lock the note for 30 minutes |
| `POST` | `/api/lock/{noteKey}/extend` | ✓ | Extend active lock by 15 minutes (max 120 min) |
| `POST` | `/api/lock/{noteKey}/unlock` | ✓ | Release the lock early |
| `POST` | `/logout` | ✓ | Sign out |

### WebSocket (STOMP over SockJS)

| Destination | Direction | Description |
|-------------|-----------|-------------|
| `/ws` | Handshake | SockJS WebSocket connection entry point |
| `/app/note/{noteKey}/edit` | Client → Server | Publish a content update |
| `/topic/note/{noteKey}` | Server → Client | Receive live content updates from other users |

---

## 🗄️ Database Schema

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
    id                 BIGSERIAL PRIMARY KEY,
    note_id            BIGINT REFERENCES notes(id) ON DELETE CASCADE,
    locked_by          VARCHAR(255) REFERENCES users(id),
    locked_until       TIMESTAMP NOT NULL,
    total_lock_minutes INT DEFAULT 0
);
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL 14+
- A Google OAuth2 application (Client ID + Secret)

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/Praveenkumar1817/SharePad.git
   cd SharePad
   ```

2. **Create the local database**
   ```bash
   psql -U postgres -c "CREATE DATABASE sharepad;"
   ```

3. **Set environment variables**

   | Variable | Default | Description |
   |---|---|---|
   | `DB_HOST` | `localhost` | PostgreSQL host |
   | `DB_PORT` | `5432` | PostgreSQL port |
   | `DB_NAME` | `sharepad` | Database name |
   | `DB_USER` | `postgres` | Database user |
   | `DB_PASS` | `postgres` | Database password |
   | `GOOGLE_CLIENT_ID` | — | Google OAuth2 client ID |
   | `GOOGLE_CLIENT_SECRET` | — | Google OAuth2 client secret |
   | `FRONTEND_URL` | `http://localhost:5500` | Allowed CORS origin |

   ```bash
   export GOOGLE_CLIENT_ID=your_client_id
   export GOOGLE_CLIENT_SECRET=your_client_secret
   ```

4. **Build and run the backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The server starts on **port 8080**. The frontend is served as static content at `http://localhost:8080`.

5. **Optional — serve the frontend separately** (e.g. with VS Code Live Server on port 5500)

   Open `frontend/index.html` with any local HTTP server. Make sure `FRONTEND_URL` includes `http://localhost:5500` so CORS is allowed.

### Docker

Build and run everything in a single container:

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

App will be available at `http://localhost:8080`.

---

## ☁️ Deployment

SharePad uses a **split deployment** strategy:

| Layer | Platform | URL |
|---|---|---|
| Frontend (static) | **Vercel** | https://sharepad-nu.vercel.app |
| Backend (Docker) | **Render** | https://sharepad-y829.onrender.com |
| Database | **Render** (managed PostgreSQL) | — |

### Deploy Backend to Render

The `render.yaml` blueprint provisions both the web service and the database automatically:

1. Push this repository to GitHub.
2. In the Render dashboard → **New Blueprint** → connect the repo.
3. Set the two secrets manually in the Render dashboard:
   - `GOOGLE_CLIENT_ID`
   - `GOOGLE_CLIENT_SECRET`
4. Set `FRONTEND_URL` to your Vercel URL (e.g. `https://sharepad-nu.vercel.app`).
5. Add the Render callback URL to your Google OAuth2 app's **Authorised redirect URIs**:
   ```
   https://<your-service>.onrender.com/login/oauth2/code/google
   ```

### Deploy Frontend to Vercel

```bash
npx vercel deploy frontend/ --prod --yes
```

Or connect the GitHub repo to Vercel and set the **Root Directory** to `frontend/`.

---

## 🔑 Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/) → **APIs & Services → Credentials**.
2. Create an **OAuth 2.0 Client ID** of type *Web application*.
3. Add **Authorised redirect URIs**:
   ```
   http://localhost:8080/login/oauth2/code/google
   https://<your-render-service>.onrender.com/login/oauth2/code/google
   ```
4. Copy the **Client ID** and **Client Secret** into your environment variables.

---

## ⚙️ How It Works

1. A user visits the app and enters a note name (or follows a direct link like `/#my-note`).
2. The frontend calls `GET /api/notes/{noteKey}` to load or create the note, including its current lock state.
3. A WebSocket connection is opened to `/ws`. The client subscribes to `/topic/note/{noteKey}` for live updates.
4. Every keystroke is published over STOMP to `/app/note/{noteKey}/edit`. The server broadcasts the new content to all other subscribers on that topic.
5. An authenticated user can lock the note. While locked, only the lock owner can edit; others see the editor in read-only mode with a **"Locked by …"** badge.
6. Locks expire automatically and are cleaned up server-side every 60 seconds.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
