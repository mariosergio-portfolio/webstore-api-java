-- Cities and suppliers. H2 version (local development only).
-- H2 has no PostGIS: "location" is stored as WKT text ('POINT(lon lat)').
-- The PostgreSQL version uses a PostGIS GEOMETRY(Point, 4326) column instead.
CREATE TABLE cities (
    id        UUID         PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    state     VARCHAR(100),
    country   VARCHAR(100) NOT NULL,
    location  VARCHAR(100) NOT NULL
);

CREATE TABLE suppliers (
    id               UUID         PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    email            VARCHAR(255),
    address_city_id  UUID         NOT NULL REFERENCES cities(id)
);

CREATE INDEX idx_suppliers_address_city_id ON suppliers(address_city_id);

INSERT INTO cities (id, name, state, country, location) VALUES
    ('7a1b0c10-0000-4000-8000-000000000001', 'São Paulo', 'SP',         'Brazil',        'POINT(-46.6333 -23.5505)'),
    ('7a1b0c10-0000-4000-8000-000000000002', 'Shenzhen',  'Guangdong',  'China',         'POINT(114.0579 22.5431)'),
    ('7a1b0c10-0000-4000-8000-000000000003', 'Austin',    'Texas',      'United States', 'POINT(-97.7431 30.2672)'),
    ('7a1b0c10-0000-4000-8000-000000000004', 'Berlin',    'Berlin',     'Germany',       'POINT(13.4050 52.5200)'),
    ('7a1b0c10-0000-4000-8000-000000000005', 'Osaka',     'Osaka',      'Japan',         'POINT(135.5023 34.6937)');
