DROP TABLE IF EXISTS ramen_order CASCADE;

CREATE TABLE ramen_order(
    id SERIAL NOT NULL,
    ramen_order jsonb
);
