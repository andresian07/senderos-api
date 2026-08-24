package com.andres.senderos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
    @NotBlank String username,
    @NotBlank @Size(min = 8, max = 16) String password
) { }
