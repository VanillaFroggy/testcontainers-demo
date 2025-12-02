package com.example.domain.dao.jdbc;

import com.example.config.OrgstructureTestDbExtension;
import com.example.domain.dao.projection.CycleDetectionProjection;
import com.example.domain.dao.projection.DivisionPositionCountProjection;
import com.example.domain.dao.projection.EmployeeAssignmentCountPerUnitProjection;
import com.example.domain.dao.projection.ExternalIdentity;
import com.example.domain.dao.projection.PositionAssignmentCountProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessRuleValidationDaoTest {

    @RegisterExtension
    static OrgstructureTestDbExtension dbExtension = new OrgstructureTestDbExtension();

    BusinessRuleValidationDao dao;
    JdbcClient jdbc;

    @BeforeEach
    void setUp() {
        jdbc = dbExtension.getJdbc();
        dao = new BusinessRuleValidationDao(jdbc);
    }

    @Test
    void findDivisionsWithNonSingleHeadPosition() {
        // Arrange
        dbExtension.runScripts("db/test-data.sql");

        // Act
        List<DivisionPositionCountProjection> projections = dao.findDivisionsWithNonSingleHeadPosition(
            List.of("test_unit")
        );

        // Assert
        assertEquals(4, projections.size());

        var firstProjection = projections.get(0);

        assertEquals(1, firstProjection.divisionId());
        assertEquals("test_structure_element_external_id_1", firstProjection.divisionExternalId());
        assertEquals(2, firstProjection.positionCount());

        var secondProjection = projections.get(1);

        assertEquals(2, secondProjection.divisionId());
        assertEquals("test_structure_element_external_id_2", secondProjection.divisionExternalId());
        assertEquals(2, secondProjection.positionCount());

        var thirdProjection = projections.get(2);

        assertEquals(3, thirdProjection.divisionId());
        assertEquals("test_structure_element_external_id_3", thirdProjection.divisionExternalId());
        assertEquals(2, thirdProjection.positionCount());

        var fourthProjection = projections.get(3);

        assertEquals(4, fourthProjection.divisionId());
        assertEquals("test_structure_element_external_id_4", fourthProjection.divisionExternalId());
        assertEquals(0, fourthProjection.positionCount());
    }

    @Test
    void findUnassignedHeadPositions() {
        // Arrange
        dbExtension.runScripts("db/test-data.sql");

        // Act
        List<ExternalIdentity> projections = dao.findUnassignedHeadPositions(
            List.of("test_unit")
        );

        // Assert
        assertEquals(1, projections.size());

        var firstProjection = projections.get(0);

        assertEquals(3, firstProjection.id());
        assertEquals("test_position_external_id_3", firstProjection.externalId());
    }

    @Test
    void findHeadPositionsWithNonSingleAssignment() {
        // Arrange
        dbExtension.runScripts("db/test-data.sql");

        // Act
        List<PositionAssignmentCountProjection> projections = dao.findHeadPositionsWithNonSingleAssignment(
            List.of("test_unit")
        );

        // Assert
        assertEquals(3, projections.size());

        var firstProjection = projections.get(0);

        assertEquals(1, firstProjection.positionId());
        assertEquals("test_position_external_id_1", firstProjection.positionExternalId());
        assertEquals(0, firstProjection.assignmentsCount());

        var secondProjection = projections.get(1);

        assertEquals(2, secondProjection.positionId());
        assertEquals("test_position_external_id_2", secondProjection.positionExternalId());
        assertEquals(0, secondProjection.assignmentsCount());

        var thirdProjection = projections.get(2);

        assertEquals(6, thirdProjection.positionId());
        assertEquals("test_position_external_id_6", thirdProjection.positionExternalId());
        assertEquals(2, thirdProjection.assignmentsCount());
    }

    @Test
    void findEmployeesWithMultipleMainAssignmentsPerUnit() {
        // Arrange
        dbExtension.runScripts("db/test-data.sql");

        // Act
        List<EmployeeAssignmentCountPerUnitProjection> projections =
            dao.findEmployeesWithMultipleMainAssignmentsPerUnit(List.of("test_unit"));

        // Assert
        assertEquals(2, projections.size());

        var firstProjection = projections.get(0);

        assertEquals(1, firstProjection.employeeId());
        assertEquals("test_employee_external_id_1", firstProjection.employeeExternalId());
        assertEquals(2, firstProjection.assignmentsCount());
        assertEquals("test_unit", firstProjection.unitCode());

        var secondProjection = projections.get(1);

        assertEquals(3, secondProjection.employeeId());
        assertEquals("test_employee_external_id_3", secondProjection.employeeExternalId());
        assertEquals(2, secondProjection.assignmentsCount());
        assertEquals("test_unit", secondProjection.unitCode());
    }

    @Test
    void findEmployeesWithoutOfficialLegalAssignmentInActiveUnits() {
        // Arrange
        dbExtension.runScripts("db/test-data.sql");

        // Act
        List<ExternalIdentity> projections =
            dao.findEmployeesWithNoMainAssignments(List.of("test_unit"));

        // Assert
        assertEquals(2, projections.size());

        var firstProjection = projections.get(0);

        assertEquals(2, firstProjection.id());
        assertEquals("test_employee_external_id_2", firstProjection.externalId());

        var secondProjection = projections.get(1);

        assertEquals(4, secondProjection.id());
        assertEquals("test_employee_external_id_4", secondProjection.externalId());
    }

    @Test
    void findCyclicDivisions() {
        // Arrange
        dbExtension.runScripts("db/test-data.sql");

        // Act
        List<CycleDetectionProjection> cycles = dao.findCyclicDivisions(List.of("test_unit"))
            .stream()
            .sorted(Comparator.comparing(CycleDetectionProjection::elementId))
            .toList();

        // Assert
        assertEquals(4, cycles.size());

        var cycle1 = cycles.get(0);
        assertEquals(1, cycle1.elementId());
        assertEquals("test_structure_element_external_id_1", cycle1.elementExternalId());
        assertEquals("1 -> 2 -> 1", cycle1.cyclePath());

        var cycle2 = cycles.get(1);
        assertEquals(2, cycle2.elementId());
        assertEquals("test_structure_element_external_id_2", cycle2.elementExternalId());
        assertEquals("2 -> 1 -> 2", cycle2.cyclePath());

        var cycle3 = cycles.get(2);
        assertEquals(3, cycle3.elementId());
        assertEquals("test_structure_element_external_id_3", cycle3.elementExternalId());
        assertEquals("3 -> 2 -> 1 -> 2", cycle3.cyclePath());

        var cycle4 = cycles.get(3);
        assertEquals(4, cycle4.elementId());
        assertEquals("test_structure_element_external_id_4", cycle4.elementExternalId());
        assertEquals("4 -> 3 -> 2 -> 1 -> 2", cycle4.cyclePath());
    }
}
