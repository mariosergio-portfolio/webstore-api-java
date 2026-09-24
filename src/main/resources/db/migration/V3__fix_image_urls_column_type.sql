ALTER TABLE products ALTER COLUMN image_urls TYPE jsonb USING image_urls::jsonb;
