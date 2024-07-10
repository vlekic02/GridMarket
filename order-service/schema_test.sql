DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payment_method') THEN
        CREATE TYPE payment_method AS ENUM ('BALANCE', 'PAYPAL');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS "order" (
    order_id SERIAL PRIMARY KEY,
    "user" INTEGER NOT NULL,
    application INTEGER NOT NULL,
    date TIMESTAMP NOT NULL,
    method payment_method NOT NULL
);