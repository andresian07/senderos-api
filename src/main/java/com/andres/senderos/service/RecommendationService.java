package com.andres.senderos.service;

import com.andres.senderos.dto.HikeHistoryResponseDto;
import com.andres.senderos.dto.RecommendationResponseDto;
import com.andres.senderos.dto.WeatherForecastDto;
import com.andres.senderos.persistence.entity.Difficulty;
import com.andres.senderos.persistence.entity.RouteEntity;
import com.andres.senderos.persistence.repository.RouteRepository;
import com.andres.senderos.web.exception.ForecastNotAvailableException;
import com.andres.senderos.web.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private final RouteRepository routeRepository;
    private final WeatherService weatherService;
    private final HikeHistoryService hikeHistoryService;

    public RecommendationService(RouteRepository routeRepository, WeatherService weatherService, HikeHistoryService hikeHistoryService) {
        this.routeRepository = routeRepository;
        this.weatherService = weatherService;
        this.hikeHistoryService = hikeHistoryService;
    }

    public RecommendationResponseDto getRecommendation(Long userId, Long routeId, LocalDate date) {
        RouteEntity route = this.routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("la ruta con el id: " + routeId + " no se encuentra"));

        WeatherForecastDto forecastForDay = this.weatherService.getForecastForRoute(routeId).stream()
                .filter(f -> f.date().equals(date))
                .findFirst()
                .orElseThrow(() -> new ForecastNotAvailableException(
                        "no hay pronóstico disponible para el " + date + " (el pronóstico solo cubre los próximos días)"));

        List<HikeHistoryResponseDto> history = this.hikeHistoryService.getHistoryForUser(userId);

        List<String> reasons = new ArrayList<>();

        boolean climaRiesgoso = forecastForDay.precipitationMm() >= 10
                || forecastForDay.description().equals("Tormenta")
                || forecastForDay.description().equals("Nieve");
        if (climaRiesgoso) {
            reasons.add("El pronóstico indica " + forecastForDay.description().toLowerCase()
                    + " (" + forecastForDay.precipitationMm() + "mm de lluvia)");
        }

        boolean esDificil = route.getDifficulty() == Difficulty.DIFICIL;
        boolean sinExperienciaDificil = history.stream()
                .noneMatch(h -> h.difficulty() == Difficulty.DIFICIL);
        if (esDificil && sinExperienciaDificil) {
            reasons.add("Nunca completaste una ruta de dificultad 'DIFICIL' antes");
        }

        String verdict = switch (reasons.size()) {
            case 0 -> "RECOMENDADO";
            case 1 -> "PRECAUCION";
            default -> "NO_RECOMENDADO";
        };

        return new RecommendationResponseDto(route.getId(), route.getName(), date, verdict, reasons);
    }
}