package io.github.ruanvasco.api.dto;

import java.time.Instant;

public record StandardError(
        Instant timestamp,
        Integer status,
        String error,
        String path
) {
}
