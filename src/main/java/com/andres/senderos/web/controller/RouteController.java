package com.andres.senderos.web.controller;

import com.andres.senderos.dto.RouteCreateDto;
import com.andres.senderos.dto.RouteResponseDto;
import com.andres.senderos.dto.RouteUpdateDto;
import com.andres.senderos.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Routes", description = "CRUD de rutas de senderismo (nombre, region, distancia, desnivel, dificultad, coordenadas).")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @Operation(summary = "Obtener una ruta por id", description = "Devuelve 404 si no existe una ruta con ese id.")
    @GetMapping("/{id}")
    public RouteResponseDto get(@PathVariable Long id) {
        return this.routeService.get(id);
    }

    @Operation(summary = "Listar todas las rutas")
    @GetMapping
    public  List<RouteResponseDto> getAll(){
        return this.routeService.getAll();
    }

    @Operation(summary = "Actualizar una ruta", description = "Actualizacion parcial: solo se aplican los campos no nulos del body. 404 si el id no existe.")
    @PutMapping("/{id}")
    public RouteResponseDto update(@PathVariable Long id, @RequestBody RouteUpdateDto routeUpdateDto){
          return this.routeService.update(id, routeUpdateDto);
    }

    @Operation(summary = "Crear una ruta")
    @PostMapping
    public RouteResponseDto create(@RequestBody RouteCreateDto routeCreateDto){
        return this.routeService.save(routeCreateDto);
    }

    @Operation(summary = "Eliminar una ruta", description = "Devuelve la ruta eliminada, o 404 si el id no existe.")
    @DeleteMapping("/{id}")
    public RouteResponseDto delete(@PathVariable Long id){
        return this.routeService.delete(id);
    }

}
