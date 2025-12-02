package com.example.domain.dao.projection;

public record DivisionPositionCountProjection(
    long divisionId,
    String divisionExternalId,
    long positionCount
) {
}
