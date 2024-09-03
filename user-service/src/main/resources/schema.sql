CREATE TABLE IF NOT EXISTS role (
  role_id SERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS grid_user (
  user_id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  surname TEXT NOT NULL,
  username TEXT NOT NULL UNIQUE,
  role INTEGER NOT NULL,
  balance DECIMAL NOT NULL CHECK (balance >= 0),
  FOREIGN KEY (role) REFERENCES role(role_id)
);

CREATE TABLE IF NOT EXISTS ban (
  ban_id SERIAL PRIMARY KEY,
  issuer INTEGER NOT NULL,
  grid_user INTEGER NOT NULL,
  "date" TIMESTAMP NOT NULL,
  reason TEXT,
  FOREIGN KEY (issuer) REFERENCES grid_user(user_id),
  FOREIGN KEY (grid_user) REFERENCES grid_user(user_id) ON DELETE CASCADE
);