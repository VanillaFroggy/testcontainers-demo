package com.example.domain.dao.projection;

public record EmployeeAssignmentCountPerUnitProjection(
    long employeeId,
    String employeeExternalId,
    long assignmentsCount,
    String unitCode
) {
}
