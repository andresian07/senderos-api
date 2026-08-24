package com.andres.senderos.dto;

import java.time.LocalDate;
import java.util.List;

public record RecommendationResponseDto(
        Long routeId,
        String routeName,
        LocalDate date,
        String verdict,
        List<String> reasons
) {}