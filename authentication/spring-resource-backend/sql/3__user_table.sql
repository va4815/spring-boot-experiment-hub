BEGIN;

CREATE TABLE t_user (
    id                      SERIAL PRIMARY KEY,
    kc_id                   VARCHAR(255),
    username                VARCHAR(255),
    email                   VARCHAR(255),
    first_name              VARCHAR(255),
    last_name               VARCHAR(255),
    is_enabled              BOOLEAN NOT NULL,
    is_email_verified       BOOLEAN NOT NULL,

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255)
);


COMMIT;