package com.example.domain.dao.projection;

public record PositionAssignmentCountProjection(
    long positionId,
    String positionExternalId,
    long assignmentsCount
) {
}
