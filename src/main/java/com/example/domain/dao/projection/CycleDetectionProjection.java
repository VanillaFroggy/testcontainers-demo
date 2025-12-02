package com.example.domain.dao.projection;

public record CycleDetectionProjection(
    long elementId,
    String elementExternalId,
    String cyclePath
) {
}
