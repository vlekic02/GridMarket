DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payment_method') THEN
        CREATE TYPE payment_method AS ENUM ('BALANCE', 'PAYPAL');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS grid_order (
    order_id SERIAL PRIMARY KEY,
    "user" INTEGER NOT NULL,
    application INTEGER NOT NULL,
    date TIMESTAMP NOT NULL,
    method payment_method NOT NULL
);

INSERT INTO grid_order VALUES (
    DEFAULT,
    2,
    2,
    '2024-01-08 04:05:06',
    'BALANCE'
);

INSERT INTO grid_order VALUES (
    DEFAULT,
    1,
    2,
    '2024-01-08 04:05:06',
    'BALANCE'
);

INSERT INTO grid_order VALUES (
    DEFAULT,
    3,
    3,
    '2024-01-08 04:05:06',
    'PAYPAL'
);

INSERT INTO grid_order VALUES (
    DEFAULT,
    5,
    5,
    '2024-01-08 04:05:06',
    'PAYPAL'
);
