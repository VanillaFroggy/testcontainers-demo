INSERT INTO structure_element(id, parent_id, external_id, unit_code)
    OVERRIDING SYSTEM VALUE
VALUES (1, NULL, 'test_structure_element_external_id_1', 'test_unit'),
       (2, 1, 'test_structure_element_external_id_2', 'test_unit'),
       (3, 2, 'test_structure_element_external_id_3', 'test_unit'),
       (4, 3, 'test_structure_element_external_id_4', 'test_unit');

UPDATE structure_element
SET parent_id = 2
WHERE id = 1;

INSERT INTO position(id, structure_element_id, external_id, is_head, date_start, date_end, unit_code, date_to)
    OVERRIDING SYSTEM VALUE
VALUES (1, 1, 'test_position_external_id_1', TRUE, '2025-01-01', NULL,  'test_unit', NULL),
       (2, 1, 'test_position_external_id_2', TRUE, '2025-01-01', NULL,  'test_unit', NULL),
       (3, 2, 'test_position_external_id_3', TRUE, '2025-01-01', NULL, 'test_unit', NULL),
       (4, 2, 'test_position_external_id_4', TRUE, '2025-01-01', NULL, 'test_unit', NULL),
       (5, 3, 'test_position_external_id_5', TRUE, '2025-01-01', NULL,  'test_unit', NULL),
       (6, 3, 'test_position_external_id_6', TRUE, '2025-01-01', NULL,  'test_unit', NULL),
       (7, 3, 'test_position_external_id_7', FALSE, '2025-01-01', NULL,  'test_unit', NULL),
       (8, 4, 'test_position_external_id_8', TRUE, '2025-01-01', NULL,  'test_unit', '2025-03-03 23:59:59.000000'),
       (9, 4, 'test_position_external_id_9', TRUE, '2025-01-01', '2025-02-02',  'test_unit', NULL);

INSERT INTO person(id, external_id)
    OVERRIDING SYSTEM VALUE
VALUES (1, 'test_person_external_id_1'),
       (2, 'test_person_external_id_2'),
       (3, 'test_person_external_id_3'),
       (4, 'test_person_external_id_4');

INSERT INTO employee(id, person_id, external_id)
    OVERRIDING SYSTEM VALUE
VALUES (1, 1, 'test_employee_external_id_1'),
       (2, 2, 'test_employee_external_id_2'),
       (3, 3, 'test_employee_external_id_3'),
       (4, 4, 'test_employee_external_id_4');

INSERT INTO position_assignment(id, employee_id, position_id, external_id, is_official_legal, date_start, date_end, unit_code)
    OVERRIDING SYSTEM VALUE
VALUES (1, 1, 1,'test_position_assignment_external_id_1', TRUE, '2025-01-01', NULL, 'test_unit'),
       (2, 1, 2,'test_position_assignment_external_id_2', TRUE, '2025-01-01', NULL, 'test_unit'),
       (3, 2, 2,'test_position_assignment_external_id_3', TRUE, '2025-01-01', '2025-02-02', 'test_unit'),
       (4, 2, 3,'test_position_assignment_external_id_4', TRUE, '2025-01-01', '2025-02-02', 'test_unit'),
       (5, 3, 4,'test_position_assignment_external_id_5', TRUE, '2025-01-01', NULL, 'test_unit'),
       (6, 3, 5,'test_position_assignment_external_id_6', TRUE, '2025-01-01', NULL, 'test_unit'),
       (7, 2, 6,'test_position_assignment_external_id_7', FALSE, '2025-01-01', NULL, 'test_unit'),
       (8, 3, 6,'test_position_assignment_external_id_8', FALSE, '2025-01-01', NULL, 'test_unit'),
       (9, 4, 7,'test_position_assignment_external_id_9', FALSE, '2025-01-01', NULL, 'test_unit');

INSERT INTO person_absence(id, person_id, external_id, date_start, date_end, unit_code)
    OVERRIDING SYSTEM VALUE
VALUES (1, 1, 'test_person_absence_external_id_1', '2025-01-01', NULL, 'test_unit'),
       (2, 2, 'test_person_absence_external_id_2', '2025-01-01', '2025-02-02', 'test_unit');
