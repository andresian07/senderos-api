package com.andres.senderos.service;

import com.andres.senderos.dto.HikeHistoryResponseDto;
import com.andres.senderos.dto.RecommendationResponseDto;
import com.andres.senderos.dto.WeatherForecastDto;
import com.andres.senderos.persistence.entity.Difficulty;
import com.andres.senderos.persistence.entity.RouteEntity;
import com.andres.senderos.persistence.repository.RouteRepository;
import com.andres.senderos.web.exception.ForecastNotAvailableException;
import com.andres.senderos.web.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// @ExtendWith(MockitoExtension.class) le dice a JUnit que active Mockito para esta clase:
// procesa las anotaciones @Mock/@InjectMocks de abajo antes de correr cada test.
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    // @Mock crea un doble falso de cada dependencia. Por defecto, cualquier método
    // que no configuremos con when(...) devuelve null / 0 / false — no toca la DB ni la red real.
    @Mock
    private RouteRepository routeRepository;

    @Mock
    private WeatherService weatherService;

    @Mock
    private HikeHistoryService hikeHistoryService;

    // @InjectMocks crea una instancia REAL de RecommendationService y le inyecta
    // los 3 mocks de arriba en su constructor. Es la clase que realmente estamos probando.
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void devuelveRecomendadoCuandoNoHayAdvertencias() {
        // Arrange: construimos los datos falsos que los mocks van a devolver.
        Long userId = 1L;
        Long routeId = 2L;
        LocalDate date = LocalDate.of(2026, 8, 25);

        RouteEntity route = new RouteEntity();
        route.setId(routeId);
        route.setName("Cerro Catedral");
        route.setDifficulty(Difficulty.MODERADA);

        WeatherForecastDto forecastDelDia = new WeatherForecastDto(
                date, 20.0, 8.0, 0.0, "Despejado");

        // when(...).thenReturn(...) programa la respuesta del mock: "cuando te llamen
        // con estos argumentos, devolvé esto", en vez de ejecutar la lógica real.
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(weatherService.getForecastForRoute(routeId)).thenReturn(List.of(forecastDelDia));
        when(hikeHistoryService.getHistoryForUser(userId)).thenReturn(List.of());

        // Act: llamamos al método real que queremos probar.
        RecommendationResponseDto resultado = recommendationService.getRecommendation(userId, routeId, date);

        // Assert: verificamos el resultado con AssertJ (ya incluido en spring-boot-starter-test).
        assertThat(resultado.verdict()).isEqualTo("RECOMENDADO");
        assertThat(resultado.reasons()).isEmpty();
    }

    // TODO: precaucionPorClimaRiesgoso
    // Ruta MODERADA (no dispara la advertencia de experiencia), pronóstico con
    // precipitationMm >= 10 (o description "Tormenta"/"Nieve"), historial vacío o con DIFICIL ya hecho.
    // Verificar: verdict == "PRECAUCION" y reasons tiene tamaño 1.

    @Test
    void precacionPorClimaRiesgoso(){
        Long userId = 1L;
        Long routeId = 2L;
        LocalDate date = LocalDate.of(2026, 8, 25);

        RouteEntity route = new RouteEntity();
        route.setId(routeId);
        route.setName("Cerro Catedral");
        route.setDifficulty(Difficulty.MODERADA);

        WeatherForecastDto forecastDelDia = new WeatherForecastDto(
                date, 20.0, 8.0, 9.0, "Tormenta");


        // when(...).thenReturn(...) programa la respuesta del mock: "cuando te llamen
        // con estos argumentos, devolvé esto", en vez de ejecutar la lógica real.
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(weatherService.getForecastForRoute(routeId)).thenReturn(List.of(forecastDelDia));
        when(hikeHistoryService.getHistoryForUser(userId)).thenReturn(List.of());

        // Act: llamamos al método real que queremos probar.
        RecommendationResponseDto resultado = recommendationService.getRecommendation(userId, routeId, date);

        // Assert: verificamos el resultado con AssertJ (ya incluido en spring-boot-starter-test).
        assertThat(resultado.verdict()).isEqualTo("PRECAUCION");
        assertThat(resultado.reasons()).hasSize(1);

    }

    // TODO: precaucionPorFaltaDeExperiencia
    // Ruta DIFICIL, clima despejado (sin advertencia climática), historial SIN ninguna ruta DIFICIL completada.
    // Verificar: verdict == "PRECAUCION" y reasons tiene tamaño 1.

    @Test
    void precaucionPorFaltaDeExperiencia(){
        Long userId = 1L;
        Long routeId = 2L;
        LocalDate date = LocalDate.of(2026, 8, 25);

        RouteEntity route = new RouteEntity();
        route.setId(routeId);
        route.setName("Cerro Catedral");
        route.setDifficulty(Difficulty.DIFICIL);

        WeatherForecastDto forecastDelDia = new WeatherForecastDto(
                date, 20.0, 8.0, 0.0, "Despejado");

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(weatherService.getForecastForRoute(routeId)).thenReturn(List.of(forecastDelDia));
        when(hikeHistoryService.getHistoryForUser(userId)).thenReturn(List.of());

        // Act: llamamos al método real que queremos probar.
        RecommendationResponseDto resultado = recommendationService.getRecommendation(userId, routeId, date);

        // Assert: verificamos el resultado con AssertJ (ya incluido en spring-boot-starter-test).
        assertThat(resultado.verdict()).isEqualTo("PRECAUCION");
        assertThat(resultado.reasons()).hasSize(1);

    }

    @Test
    void noRecomendodoCuandoHayDosAdvertencias(){
        Long userId = 1L;
        Long routeId = 2L;
        LocalDate date = LocalDate.of(2026, 8, 25);

        RouteEntity route = new RouteEntity();
        route.setId(userId);
        route.setName("Cerro Catedral");
        route.setDifficulty(Difficulty.DIFICIL);

        WeatherForecastDto forecastDelDia = new WeatherForecastDto(
                date, 20.0, 8.0, 10.0, "Nieve"
        );

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(weatherService.getForecastForRoute(routeId)).thenReturn(List.of(forecastDelDia));
        when(hikeHistoryService.getHistoryForUser(userId)).thenReturn(List.of());

        RecommendationResponseDto resultado = recommendationService.getRecommendation(userId, routeId, date);

        // Assert: verificamos el resultado con AssertJ (ya incluido en spring-boot-starter-test).
        assertThat(resultado.verdict()).isEqualTo("NO_RECOMENDADO");
        assertThat(resultado.reasons()).hasSize(2);

    }

    @Test
    void lanzaNotFoundExceptionSiLaRutaNoExiste(){
        Long userId = 1L;
        Long routeId = 2L;
        LocalDate date = LocalDate.of(2026, 8, 25);

        RouteEntity route = new RouteEntity();
        route.setId(userId);
        route.setName("Cerro Catedral");
        route.setDifficulty(Difficulty.DIFICIL);

        WeatherForecastDto forecastDelDia = new WeatherForecastDto(
                date, 20.0, 8.0, 10.0, "Nieve"
        );

        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        // Assert: verificamos el resultado con AssertJ (ya incluido en spring-boot-starter-test).
        assertThatThrownBy(() -> recommendationService.getRecommendation(userId, routeId, date))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    void lanzaForecastNotAvailableExceptionSiNoHayPronosticoParaLaFecha(){
        Long userId = 1L;
        Long routeId = 2L;
        LocalDate date = LocalDate.of(2026, 8, 25);

        RouteEntity route = new RouteEntity();
        route.setId(userId);
        route.setName("Cerro Catedral");
        route.setDifficulty(Difficulty.DIFICIL);

        WeatherForecastDto forecastDelDia = new WeatherForecastDto(
                date, 20.0, 8.0, 10.0, "Nieve"
        );

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(weatherService.getForecastForRoute(routeId)).thenReturn(List.of(forecastDelDia));

        // Assert: verificamos el resultado con AssertJ (ya incluido en spring-boot-starter-test).
        assertThatThrownBy(() -> recommendationService.getRecommendation(userId, routeId, date.plusDays(3))).isInstanceOf(ForecastNotAvailableException.class);
    }

    // TODO: lanzaForecastNotAvailableExceptionSiNoHayPronosticoParaLaFecha
    // weatherService.getForecastForRoute(...) devuelve una lista con fechas distintas a `date`
    // (el .filter(...).findFirst() del service no encuentra nada). Verificar la excepción con assertThatThrownBy.
}