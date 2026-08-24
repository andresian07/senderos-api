package com.andres.senderos.web.controller;

import com.andres.senderos.dto.WeatherForecastDto;
import com.andres.senderos.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/routes/{routeId}/weather")
@Tag(name = "Weather", description = "Pronostico del clima (Open-Meteo) para las coordenadas de una ruta, cacheado por ruta.")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(summary = "Pronostico de una ruta", description = "Devuelve el pronostico dia por dia para las coordenadas de la ruta. 400 si la ruta no tiene coordenadas cargadas.")
    @GetMapping
    public List<WeatherForecastDto> getForecast(@PathVariable Long routeId) {
        return this.weatherService.getForecastForRoute(routeId);
    }
}
