CREATE TABLE test_abstract
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    name               VARCHAR(256)                        NOT NULL,
    date_from          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_to            TIMESTAMP,
    update_date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    author_employee_id BIGINT                              NOT NULL,
    update_employee_id BIGINT                              NOT NULL,
    external_id        VARCHAR(128) UNIQUE
);
