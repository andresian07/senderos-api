package com.andres.senderos.service;

import com.andres.senderos.client.OpenMeteoClient;
import com.andres.senderos.client.dto.OpenMeteoResponseDto;
import com.andres.senderos.dto.WeatherForecastDto;
import com.andres.senderos.persistence.entity.RouteEntity;
import com.andres.senderos.persistence.repository.RouteRepository;
import com.andres.senderos.web.exception.MissingCoordinatesException;
import com.andres.senderos.web.exception.NotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class WeatherService {

    private final RouteRepository routeRepository;
    private final OpenMeteoClient openMeteoClient;

    public WeatherService(RouteRepository routeRepository, OpenMeteoClient openMeteoClient) {
        this.routeRepository = routeRepository;
        this.openMeteoClient = openMeteoClient;
    }

    @Cacheable(value = "weatherForecast", key = "#routeId")
    public List<WeatherForecastDto> getForecastForRoute(Long routeId) {
        RouteEntity route = this.routeRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundException("la ruta con el id: " + routeId + " no se encuentra"));

        if (route.getLatitude() == null || route.getLongitude() == null) {
            throw new MissingCoordinatesException(
                    "la ruta con el id: " + routeId + " no tiene coordenadas cargadas");
        }

        OpenMeteoResponseDto response = this.openMeteoClient.getForecast(route.getLatitude(), route.getLongitude());

        return this.toForecastList(response);
    }

    private List<WeatherForecastDto> toForecastList(OpenMeteoResponseDto response) {
        OpenMeteoResponseDto.DailyDto daily = response.daily();
        int days = daily.time().size();

        return IntStream.range(0, days)
                .mapToObj(i -> new WeatherForecastDto(
                        LocalDate.parse(daily.time().get(i)),
                        daily.temperatureMaxC().get(i),
                        daily.temperatureMinC().get(i),
                        daily.precipitationSumMm().get(i),
                        this.describeWeatherCode(daily.weatherCode().get(i))
                ))
                .toList();
    }

    private String describeWeatherCode(int code) {
        if (code == 0) return "Despejado";
        if (code <= 3) return "Parcialmente nublado";
        if (code == 45 || code == 48) return "Niebla";
        if (code <= 57) return "Llovizna";
        if (code <= 67) return "Lluvia";
        if (code <= 77) return "Nieve";
        if (code <= 82) return "Chubascos";
        return "Tormenta";
    }
}
