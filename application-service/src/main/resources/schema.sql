DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'discount_type') THEN
        CREATE TYPE discount_type AS ENUM ('FLAT', 'PERCENTAGE');
    END IF;
END$$@@

CREATE TABLE IF NOT EXISTS discount (
  discount_id SERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE,
  type discount_type NOT NULL,
  value DECIMAL NOT NULL CHECK (value > 0),
  start_date TIMESTAMP,
  end_date TIMESTAMP,
  grid_user INTEGER NOT NULL
)@@

CREATE TABLE IF NOT EXISTS application (
  application_id SERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE,
  description TEXT,
  path TEXT NOT NULL,
  publisher INTEGER NOT NULL,
  price DECIMAL NOT NULL CHECK (price >= 0),
  discount INTEGER,
  FOREIGN KEY (discount) REFERENCES discount(discount_id) ON DELETE SET NULL
)@@

CREATE TABLE IF NOT EXISTS sellable_application (
  application INTEGER PRIMARY KEY,
  start_date TIMESTAMP,
  end_date TIMESTAMP,
  FOREIGN KEY (application) REFERENCES application(application_id) ON DELETE CASCADE
)@@

CREATE TABLE IF NOT EXISTS ownership (
  grid_user INTEGER,
  application INTEGER,
  PRIMARY KEY (grid_user, application),
  FOREIGN KEY (application) REFERENCES application(application_id) ON DELETE CASCADE
)@@

CREATE TABLE IF NOT EXISTS review (
  review_id SERIAL PRIMARY KEY,
  author INTEGER NOT NULL,
  message TEXT,
  stars SMALLINT NOT NULL CHECK (stars > 0 AND stars <= 5),
  application INTEGER NOT NULL,
  UNIQUE (author, application),
  FOREIGN KEY (application) REFERENCES application(application_id) ON DELETE CASCADE
)@@

-- TODO: add indexes