package com.andres.senderos.web.controller;

import com.andres.senderos.config.CustomUserDetails;
import com.andres.senderos.dto.HikeHistoryCreateDto;
import com.andres.senderos.dto.HikeHistoryResponseDto;
import com.andres.senderos.service.HikeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hikes")
@Tag(name = "Hike History", description = "Historial de rutas completadas por el usuario autenticado. El usuario siempre se toma del JWT, nunca del request.")
public class HikeHistoryController {
    private final HikeHistoryService hikeHistoryService;

    public HikeHistoryController(HikeHistoryService hikeHistoryService) {
        this.hikeHistoryService = hikeHistoryService;
    }

    @Operation(summary = "Registrar una ruta completada", description = "Crea un registro de historial para el usuario autenticado (JWT).")
    @PostMapping
    public HikeHistoryResponseDto save(@Valid @RequestBody HikeHistoryCreateDto dto,
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        return this.hikeHistoryService.save(dto, principal.getId());
    }

    @Operation(summary = "Mi historial de rutas", description = "Devuelve el historial del usuario autenticado (JWT).")
    @GetMapping("/me")
    public List<HikeHistoryResponseDto> getHistoryForUser(@AuthenticationPrincipal CustomUserDetails principal) {
        return this.hikeHistoryService.getHistoryForUser(principal.getId());
    }
}