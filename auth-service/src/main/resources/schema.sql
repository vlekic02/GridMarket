CREATE TABLE IF NOT EXISTS "user" (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  username TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL
);

CREATE INDEX user_username_idx
ON "user" (username);