CREATE TABLE route (
    id_route         BIGSERIAL PRIMARY KEY,
    name             VARCHAR(120) NOT NULL,
    region           VARCHAR(120) NOT NULL,
    distance_km      NUMERIC(6,2) NOT NULL,
    elevation_gain_m INTEGER NOT NULL,
    difficulty       VARCHAR(20) NOT NULL
);