package com.andres.senderos.web.controller;

import com.andres.senderos.config.CustomUserDetails;
import com.andres.senderos.dto.RecommendationResponseDto;
import com.andres.senderos.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/recommendation")
@Tag(name = "Recommendation", description = "El corazon del proyecto: cruza dificultad de la ruta, pronostico del dia elegido e historial del usuario autenticado en un veredicto.")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Operation(summary = "Obtener recomendacion para una ruta y fecha",
            description = "Devuelve RECOMENDADO / PRECAUCION / NO_RECOMENDADO junto con los motivos, cruzando " +
                    "dificultad, pronostico del dia (solo cubre los proximos dias) e historial del usuario autenticado. " +
                    "404 si la ruta no existe, 400 si no hay pronostico disponible para la fecha.")
    @GetMapping
    public RecommendationResponseDto getRecommendation(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam Long routeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return this.recommendationService.getRecommendation(principal.getId(), routeId, date);
    }
}