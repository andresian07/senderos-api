CREATE TABLE hike_history (
                         id_hike_history  BIGSERIAL PRIMARY KEY,
                         id_user          BIGINT NOT NULL REFERENCES users(id_user),
                         id_route         BIGINT NOT NULL REFERENCES route(id_route),
                         completed_at     DATE NOT NULL
);




