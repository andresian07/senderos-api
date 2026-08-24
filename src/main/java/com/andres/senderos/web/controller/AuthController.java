package com.andres.senderos.web.controller;

import com.andres.senderos.dto.AuthResponseDto;
import com.andres.senderos.dto.LoginRequestDto;
import com.andres.senderos.dto.RegisterRequestDto;
import com.andres.senderos.persistence.entity.UserEntity;
import com.andres.senderos.persistence.repository.UserRepository;
import com.andres.senderos.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Registro e inicio de sesion. Publico, no requiere token.")
@SecurityRequirements
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Registrar un usuario nuevo", description = "Crea el usuario (rol USER fijo) y devuelve un JWT ya autenticado.")
    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody RegisterRequestDto dto) {
        UserEntity user = new UserEntity();
        user.setUsername(dto.username());
        user.setPassword(this.passwordEncoder.encode(dto.password()));
        user.setRole("USER");
        this.userRepository.save(user);

        return new AuthResponseDto(this.jwtService.generateToken(toUserDetails(user)));
    }

    @Operation(summary = "Iniciar sesion", description = "Valida credenciales y devuelve un JWT. 403 si son incorrectas.")
    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto dto) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        UserEntity user = this.userRepository.findByUsername(dto.username()).orElseThrow();

        return new AuthResponseDto(this.jwtService.generateToken(toUserDetails(user)));
    }

    private UserDetails toUserDetails(UserEntity user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}