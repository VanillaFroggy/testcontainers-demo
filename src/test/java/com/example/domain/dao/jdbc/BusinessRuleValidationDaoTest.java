package com.example.domain.dao.jdbc;

import com.example.domain.dao.projection.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BusinessRuleValidationDaoTest {

    BusinessRuleValidationDao dummyDao;

    @BeforeEach
    void setUp() {
        dummyDao = mock(BusinessRuleValidationDao.class);
    }

    @Test
    void findDivisionsWithNonSingleHeadPosition() {
        // Arrange
        var projection1 = new DivisionPositionCountProjection(
            1,
            "test_structure_element_external_id_1",
            2
        );
        var projection2 = new DivisionPositionCountProjection(
            2,
            "test_structure_element_external_id_2",
            2
        );
        var projection3 = new DivisionPositionCountProjection(
            3,
            "test_structure_element_external_id_3",
            2
        );
        var projection4 = new DivisionPositionCountProjection(
            4,
            "test_structure_element_external_id_4",
            0
        );

        when(dummyDao.findDivisionsWithNonSingleHeadPosition(List.of("test_unit")))
            .thenReturn(List.of(projection1, projection2, projection3, projection4));

        // Act
        List<DivisionPositionCountProjection> projections = dummyDao.findDivisionsWithNonSingleHeadPosition(
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
        var projection1 = new ExternalIdentity(3, "test_position_external_id_3");

        when(dummyDao.findUnassignedHeadPositions(List.of("test_unit")))
            .thenReturn(List.of(projection1));

        // Act
        List<ExternalIdentity> projections = dummyDao.findUnassignedHeadPositions(
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
        var projection1 = new PositionAssignmentCountProjection(
            1,
            "test_position_external_id_1",
            0
        );
        var projection2 = new PositionAssignmentCountProjection(
            2,
            "test_position_external_id_2",
            0
        );
        var projection3 = new PositionAssignmentCountProjection(
            6,
            "test_position_external_id_6",
            2
        );

        when(dummyDao.findHeadPositionsWithNonSingleAssignment(List.of("test_unit")))
            .thenReturn(List.of(projection1, projection2, projection3));

        // Act
        List<PositionAssignmentCountProjection> projections = dummyDao.findHeadPositionsWithNonSingleAssignment(
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
        var projection1 = new EmployeeAssignmentCountPerUnitProjection(
            1,
            "test_employee_external_id_1",
            2,
            "test_unit"
        );
        var projection2 = new EmployeeAssignmentCountPerUnitProjection(
            3,
            "test_employee_external_id_3",
            2,
            "test_unit"
        );

        when(dummyDao.findEmployeesWithMultipleMainAssignmentsPerUnit(List.of("test_unit")))
            .thenReturn(List.of(projection1, projection2));

        // Act
        List<EmployeeAssignmentCountPerUnitProjection> projections =
            dummyDao.findEmployeesWithMultipleMainAssignmentsPerUnit(List.of("test_unit"));

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
        var projection1 = new ExternalIdentity(2, "test_employee_external_id_2");
        var projection2 = new ExternalIdentity(4, "test_employee_external_id_4");

        when(dummyDao.findEmployeesWithNoMainAssignments(List.of("test_unit")))
            .thenReturn(List.of(projection1, projection2));

        // Act
        List<ExternalIdentity> projections =
            dummyDao.findEmployeesWithNoMainAssignments(List.of("test_unit"));

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
        var projection1 = new CycleDetectionProjection(
            1,
            "test_structure_element_external_id_1",
            "1 -> 2 -> 1"
        );
        var projection2 = new CycleDetectionProjection(
            2,
            "test_structure_element_external_id_2",
            "2 -> 1 -> 2"
        );
        var projection3 = new CycleDetectionProjection(
            3,
            "test_structure_element_external_id_3",
            "3 -> 2 -> 1 -> 2"
        );
        var projection4 = new CycleDetectionProjection(
            4,
            "test_structure_element_external_id_4",
            "4 -> 3 -> 2 -> 1 -> 2"
        );

        when(dummyDao.findCyclicDivisions(List.of("test_unit")))
            .thenReturn(List.of(projection1, projection2, projection3, projection4));

        // Act
        List<CycleDetectionProjection> cycles = dummyDao.findCyclicDivisions(List.of("test_unit"))
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
