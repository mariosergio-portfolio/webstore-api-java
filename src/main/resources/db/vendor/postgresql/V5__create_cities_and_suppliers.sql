-- Cities (with PostGIS location) and suppliers. PostgreSQL version.
-- Requires the PostGIS extension to be available on the server.
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE cities (
    id        UUID                  PRIMARY KEY,
    name      VARCHAR(255)          NOT NULL,
    state     VARCHAR(100),
    country   VARCHAR(100)          NOT NULL,
    location  GEOMETRY(Point, 4326) NOT NULL   -- longitude/latitude (WGS 84)
);

CREATE INDEX idx_cities_location ON cities USING GIST (location);

CREATE TABLE suppliers (
    id               UUID         PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    email            VARCHAR(255),
    address_city_id  UUID         NOT NULL REFERENCES cities(id)
);

CREATE INDEX idx_suppliers_address_city_id ON suppliers(address_city_id);

INSERT INTO cities (id, name, state, country, location) VALUES
    ('7a1b0c10-0000-4000-8000-000000000001', 'São Paulo', 'SP',         'Brazil',        ST_SetSRID(ST_MakePoint(-46.6333, -23.5505), 4326)),
    ('7a1b0c10-0000-4000-8000-000000000002', 'Shenzhen',  'Guangdong',  'China',         ST_SetSRID(ST_MakePoint(114.0579,  22.5431), 4326)),
    ('7a1b0c10-0000-4000-8000-000000000003', 'Austin',    'Texas',      'United States', ST_SetSRID(ST_MakePoint(-97.7431,  30.2672), 4326)),
    ('7a1b0c10-0000-4000-8000-000000000004', 'Berlin',    'Berlin',     'Germany',       ST_SetSRID(ST_MakePoint( 13.4050,  52.5200), 4326)),
    ('7a1b0c10-0000-4000-8000-000000000005', 'Osaka',     'Osaka',      'Japan',         ST_SetSRID(ST_MakePoint(135.5023,  34.6937), 4326));
