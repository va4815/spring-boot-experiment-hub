BEGIN;

CREATE TABLE t_restaurant (
                              id              SERIAL PRIMARY KEY,
                              name            VARCHAR(255),
                              restaurant_type VARCHAR(255),
                              location        VARCHAR(255),

                              created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              created_by      VARCHAR(255),
                              updated_by      VARCHAR(255)
);

CREATE TABLE t_menu (
                        id            SERIAL PRIMARY KEY,
                        restaurant_id INTEGER NOT NULL,
                        is_active     BOOLEAN NOT NULL DEFAULT TRUE,

                        created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        created_by    VARCHAR(255),
                        updated_by    VARCHAR(255),

                        CONSTRAINT fk_menu_restaurant
                            FOREIGN KEY (restaurant_id)
                                REFERENCES t_restaurant (id)
                                ON DELETE RESTRICT
);

CREATE INDEX idx_menu_restaurant_id
    ON t_menu (restaurant_id);


CREATE TABLE t_menu_item (
                             id          SERIAL PRIMARY KEY,
                             menu_id     INTEGER NOT NULL,

                             name        VARCHAR(255),
                             description VARCHAR(255),
                             item_type   VARCHAR(255),
                             category    VARCHAR(255),
                             price       DECIMAL,

                             created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             created_by  VARCHAR(255),
                             updated_by  VARCHAR(255),

                             CONSTRAINT fk_menu_item_menu
                                 FOREIGN KEY (menu_id)
                                     REFERENCES t_menu (id)
                                     ON DELETE RESTRICT
);

CREATE INDEX idx_menu_item_menu_id
    ON t_menu_item (menu_id);


CREATE TABLE t_order (
                         id            SERIAL PRIMARY KEY,
                         restaurant_id INTEGER NOT NULL,
                         total_price   DECIMAL,

                         created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         created_by    VARCHAR(255),
                         updated_by    VARCHAR(255),

                         CONSTRAINT fk_order_restaurant
                             FOREIGN KEY (restaurant_id)
                                 REFERENCES t_restaurant (id)
                                 ON DELETE RESTRICT
);

CREATE INDEX idx_order_restaurant_id
    ON t_order (restaurant_id);


CREATE TABLE t_order_item (
                              id           SERIAL PRIMARY KEY,
                              order_id     INTEGER NOT NULL,
                              menu_item_id INTEGER NOT NULL,

                              price        DECIMAL,
                              quantity     INTEGER,

                              created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              created_by   VARCHAR(255),
                              updated_by   VARCHAR(255),

                              CONSTRAINT fk_order_item_order
                                  FOREIGN KEY (order_id)
                                      REFERENCES t_order (id)
                                      ON DELETE RESTRICT,

                              CONSTRAINT fk_order_item_menu_item
                                  FOREIGN KEY (menu_item_id)
                                      REFERENCES t_menu_item (id)
                                      ON DELETE RESTRICT
);

CREATE INDEX idx_order_item_order_id
    ON t_order_item (order_id);

CREATE INDEX idx_order_item_menu_item_id
    ON t_order_item (menu_item_id);

COMMIT;