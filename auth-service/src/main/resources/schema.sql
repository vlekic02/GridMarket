CREATE TABLE IF NOT EXISTS grid_user (
  id SERIAL PRIMARY KEY,
  username TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL
);

CREATE INDEX user_username_idx
ON grid_user (username);