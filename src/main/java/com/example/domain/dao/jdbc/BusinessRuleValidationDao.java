package com.example.domain.dao.jdbc;

import com.example.domain.dao.projection.CycleDetectionProjection;
import com.example.domain.dao.projection.DivisionPositionCountProjection;
import com.example.domain.dao.projection.EmployeeAssignmentCountPerUnitProjection;
import com.example.domain.dao.projection.ExternalIdentity;
import com.example.domain.dao.projection.PositionAssignmentCountProjection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class BusinessRuleValidationDao {

    private final JdbcClient jdbc;

    public BusinessRuleValidationDao(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Находит подразделения, у которых количество руководящих позиций не равно 1.
     *
     * <p>Значение {@code position_count} будет
     * или 0, или 2 и более.
     */
    public List<DivisionPositionCountProjection> findDivisionsWithNonSingleHeadPosition(
        Collection<String> unitCodes
    ) {
        return jdbc.sql("""
                SELECT s_e.id          AS division_id,
                       s_e.external_id AS division_external_id,
                       COUNT(p.id)     AS position_count
                FROM structure_element s_e
                    LEFT JOIN position p ON p.structure_element_id = s_e.id
                        AND p.is_head IS TRUE
                        AND (p.date_to IS NULL
                            AND (p.date_start IS NULL OR p.date_start <= CURRENT_DATE)
                            AND (p.date_end IS NULL OR p.date_end >= CURRENT_DATE))
                WHERE s_e.unit_code IN (:unit_codes)
                    AND (s_e.date_to IS NULL
                        AND (s_e.date_start IS NULL OR s_e.date_start <= CURRENT_DATE)
                        AND (s_e.date_end IS NULL OR s_e.date_end >= CURRENT_DATE))
                GROUP BY s_e.id
                HAVING COUNT(p.id) != 1;
                """)
            .param("unit_codes", unitCodes)
            .query(DivisionPositionCountProjection.class)
            .list();
    }

    /**
     * Находит руководящие позиции, у которых нет действующих назначений.
     */
    public List<ExternalIdentity> findUnassignedHeadPositions(Collection<String> unitCodes) {
        return jdbc.sql("""
                SELECT p.id,
                       p.external_id
                FROM position p
                    LEFT JOIN position_assignment p_a ON p_a.position_id = p.id
                        AND (p_a.date_to IS NULL
                            AND p_a.date_start <= CURRENT_DATE
                            AND (p_a.date_end IS NULL OR p_a.date_end >= CURRENT_DATE))
                WHERE p.is_head IS TRUE
                    AND p.unit_code IN (:unit_codes)
                    AND (p.date_to IS NULL
                        AND (p.date_start IS NULL OR p.date_start <= CURRENT_DATE)
                        AND (p.date_end IS NULL OR p.date_end >= CURRENT_DATE))
                GROUP BY p.id
                HAVING COUNT(DISTINCT(p_a.id)) = 0;
                """)
            .param("unit_codes", unitCodes)
            .query(ExternalIdentity.class)
            .list();
    }

    /**
     * Находит руководящие позиции, у которых количество действующих назначений
     * (с учетом отсутствия сотрудника) не равно 1.
     * findHeadPositionsWithNonSingleAssignment
     */
    public List<PositionAssignmentCountProjection> findHeadPositionsWithNonSingleAssignment(
        Collection<String> unitCodes
    ) {
        return jdbc.sql("""
                SELECT position.id                                AS position_id,
                       position.external_id                       AS position_external_id,
                       -- Считаем разницу между общим количеством назначений и
                       -- сколько из них на отсутствующего сотрудника
                       (COUNT(assignment.id) - COUNT(absence.id)) AS assignments_count
                FROM position
                     INNER JOIN position_assignment assignment ON assignment.position_id = position.id
                         AND assignment.date_to IS NULL
                         AND assignment.date_start <= CURRENT_DATE
                         AND (assignment.date_end IS NULL OR assignment.date_end >= CURRENT_DATE)
                     INNER JOIN employee ON employee.id = assignment.employee_id
                         AND employee.date_to IS NULL
                         AND (employee.date_start IS NULL OR employee.date_start <= CURRENT_DATE)
                         AND (employee.date_end IS NULL OR employee.date_end >= CURRENT_DATE)
                     LEFT JOIN person_absence absence ON absence.person_id = employee.person_id
                         AND absence.date_to IS NULL
                         AND absence.date_start <= CURRENT_DATE
                         AND (absence.date_end IS NULL OR absence.date_end >= CURRENT_DATE)
                WHERE position.is_head = TRUE
                    AND position.unit_code IN (:unit_codes)
                    AND position.date_to IS NULL
                    AND (position.date_start IS NULL OR position.date_start <= CURRENT_DATE)
                    AND (position.date_end IS NULL OR position.date_end >= CURRENT_DATE)
                GROUP BY position.id
                HAVING COUNT(assignment.id) - COUNT(absence.id) != 1;
                """)
            .param("unit_codes", unitCodes)
            .query(PositionAssignmentCountProjection.class)
            .list();
    }

    /**
     * Находит сотрудников, у которых несколько основных назначений в рамках одного юнита.
     */
    public List<EmployeeAssignmentCountPerUnitProjection> findEmployeesWithMultipleMainAssignmentsPerUnit(
        Collection<String> unitCodes
    ) {
        return jdbc.sql("""
                SELECT employee.id            AS employee_id,
                       employee.external_id   AS employee_external_id,
                       COUNT(assignment.id)   AS assignments_count,
                       division.unit_code     AS unit_code
                FROM employee
                    INNER JOIN position_assignment assignment ON assignment.employee_id = employee.id
                    INNER JOIN position ON position.id = assignment.position_id
                    INNER JOIN structure_element division ON position.structure_element_id = division.id
                WHERE assignment.is_official_legal IS TRUE
                    AND assignment.unit_code IN (:unit_codes)
                    AND (employee.date_to IS NULL
                        AND (employee.date_start IS NULL OR employee.date_start <= CURRENT_DATE)
                        AND (employee.date_end IS NULL OR employee.date_end >= CURRENT_DATE))
                    AND (assignment.date_to IS NULL
                        AND assignment.date_start <= CURRENT_DATE
                        AND (assignment.date_end IS NULL OR assignment.date_end >= CURRENT_DATE))
                    AND (position.date_to IS NULL
                        AND (position.date_start IS NULL OR position.date_start <= CURRENT_DATE)
                        AND (position.date_end IS NULL OR position.date_end >= CURRENT_DATE))
                    AND (division.date_to IS NULL
                        AND (division.date_start IS NULL OR division.date_start <= CURRENT_DATE)
                        AND (division.date_end IS NULL OR division.date_end >= CURRENT_DATE))
                GROUP BY division.unit_code, employee.id
                HAVING COUNT(assignment.id) > 1;
                """)
            .param("unit_codes", unitCodes)
            .query(EmployeeAssignmentCountPerUnitProjection.class)
            .list();
    }

    /**
     * Находит сотрудников, у которых отсутствует хотя бы одно основное назначение в данных системы.
     */
    public List<ExternalIdentity> findEmployeesWithNoMainAssignments(Collection<String> unitCodes) {
        return jdbc.sql("""
                SELECT e.id,
                       e.external_id
                FROM employee e
                    LEFT JOIN position_assignment p_ass ON p_ass.employee_id = e.id
                        AND (p_ass.date_to IS NULL
                            AND p_ass.date_start <= CURRENT_DATE
                            AND (p_ass.date_end IS NULL OR p_ass.date_end >= CURRENT_DATE))
                            AND p_ass.unit_code IN (:unit_codes)
                            AND p_ass.is_official_legal IS TRUE
                    LEFT JOIN position p ON p.id = p_ass.position_id
                        AND (p.date_to IS NULL
                            AND (p.date_start IS NULL OR p.date_start <= CURRENT_DATE)
                            AND (p.date_end IS NULL OR p.date_end >= CURRENT_DATE))
                    LEFT JOIN structure_element s_e ON p.structure_element_id = s_e.id
                        AND (s_e.date_to IS NULL
                            AND (s_e.date_start IS NULL OR s_e.date_start <= CURRENT_DATE)
                            AND (s_e.date_end IS NULL OR s_e.date_end >= CURRENT_DATE))
                WHERE p_ass.id IS NULL
                    AND e.id > 0
                    AND (e.date_to IS NULL
                        AND (e.date_start IS NULL OR e.date_start <= CURRENT_DATE)
                        AND (e.date_end IS NULL OR e.date_end >= CURRENT_DATE));
                """)
            .param("unit_codes", unitCodes)
            .query(ExternalIdentity.class)
            .list();
    }

    public List<CycleDetectionProjection> findCyclicDivisions(Collection<String> unitCodes) {
        return jdbc.sql("""
                WITH RECURSIVE walk AS (
                    -- Начинаем с каждого элемента, у которого есть parent_id
                    SELECT id AS start_id,
                           id,
                           parent_id,
                           ARRAY[id] AS path,
                           external_id
                    FROM structure_element
                    WHERE unit_code IN (:unit_codes)
                
                    UNION ALL
                
                    -- Рекурсивно поднимаемся
                    SELECT w.start_id,
                           parent.id,
                           parent.parent_id,
                           w.path || parent.id, -- добавляем текущий узел в путь
                           w.external_id
                    FROM walk w
                             JOIN structure_element parent ON parent.id = w.parent_id
                    -- идём только если ещё не встречали этот id в пути
                    WHERE NOT parent.id = ANY(w.path)
                )
                -- Находим записи, где следующий parent_id уже есть в пути → цикл
                SELECT DISTINCT w.start_id                                     AS start_id,
                                external_id                                    AS start_external_id,
                                ARRAY_TO_STRING(w.path || w.parent_id, ' -> ') AS cycle_path
                FROM walk w
                WHERE w.parent_id IS NOT NULL
                    AND w.parent_id = ANY(w.path);
                """)
            .param("unit_codes", unitCodes)
            .query((resultSet, rowNum) -> new CycleDetectionProjection(
                resultSet.getLong("start_id"),
                resultSet.getString("start_external_id"),
                resultSet.getString("cycle_path")
            ))
            .list();
    }
}
