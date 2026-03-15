CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS notes (
    id BIGSERIAL PRIMARY KEY,
    note_key VARCHAR(255) UNIQUE NOT NULL,
    content TEXT,
    owner_id VARCHAR(255) REFERENCES users(id),
    expires_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS note_locks (
    id BIGSERIAL PRIMARY KEY,
    note_id BIGINT REFERENCES notes(id) ON DELETE CASCADE,
    locked_by VARCHAR(255) REFERENCES users(id),
    locked_until TIMESTAMP NOT NULL,
    total_lock_minutes INT DEFAULT 0
);
