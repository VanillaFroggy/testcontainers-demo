CREATE TABLE structure_element
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    date_from   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_to     TIMESTAMP,
    parent_id   BIGINT,
    external_id VARCHAR(128),
    date_start  DATE,
    date_end    DATE,
    unit_code   VARCHAR(128)                        NOT NULL,
    CONSTRAINT structure_element_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES structure_element,
    CONSTRAINT structure_element_external_id_unit_code_key UNIQUE (external_id, unit_code)
);

CREATE INDEX structure_element_parent_id_idx ON structure_element (parent_id);

CREATE TABLE position
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    date_from            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_to              TIMESTAMP,
    structure_element_id BIGINT                              NOT NULL,
    external_id          VARCHAR(128),
    is_head              BOOLEAN   DEFAULT FALSE             NOT NULL,
    date_start           DATE,
    date_end             DATE,
    unit_code            VARCHAR(128)                        NOT NULL,
    CONSTRAINT position_structure_element_id_fkey FOREIGN KEY (structure_element_id) REFERENCES structure_element,
    CONSTRAINT position_external_id_unit_code_key UNIQUE (external_id, unit_code)
);

CREATE INDEX position_structure_element_id_idx ON position (structure_element_id);

CREATE TABLE person
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    external_id VARCHAR(128) UNIQUE,
    date_from   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_to     TIMESTAMP,
    date_start  DATE,
    date_end    DATE
);

CREATE TABLE employee
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    date_from   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_to     TIMESTAMP,
    person_id   BIGINT                              NOT NULL,
    external_id VARCHAR(128) UNIQUE,
    date_start  DATE,
    date_end    DATE,
    CONSTRAINT employee_person_id_fkey FOREIGN KEY (person_id) REFERENCES person
);

CREATE INDEX employee_person_id_idx ON employee (person_id);

CREATE TABLE position_assignment
(
    id                BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    date_from         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_to           TIMESTAMP,
    employee_id       BIGINT                              NOT NULL,
    position_id       BIGINT                              NOT NULL,
    external_id       VARCHAR(128),
    is_official_legal BOOLEAN   DEFAULT FALSE             NOT NULL,
    date_start        DATE                                NOT NULL,
    date_end          DATE,
    unit_code         VARCHAR(128)                        NOT NULL,
    CONSTRAINT position_assignment_position_id_fkey FOREIGN KEY (position_id) REFERENCES position,
    CONSTRAINT position_assignment_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES employee,
    CONSTRAINT position_assignment_external_id_unit_code_key UNIQUE (external_id, unit_code)
);

CREATE INDEX position_assignment_employee_id_idx ON position_assignment (employee_id);

CREATE INDEX position_assignment_position_id_idx ON position_assignment (position_id);

CREATE TABLE person_absence
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    date_from   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    date_to     TIMESTAMP,
    person_id   BIGINT                              NOT NULL,
    date_start  DATE                                NOT NULL,
    date_end    DATE,
    external_id VARCHAR(128),
    unit_code   VARCHAR(128)                        NOT NULL,
    CONSTRAINT person_absence_person_id_fkey FOREIGN KEY (person_id) REFERENCES person,
    CONSTRAINT person_absence_external_id_unit_code_key UNIQUE (external_id, unit_code)
);

CREATE INDEX person_absence_person_id_idx ON person_absence (person_id);
