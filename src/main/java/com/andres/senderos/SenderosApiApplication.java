package com.andres.senderos;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
        info = @Info(
                title = "Senderos API",
                version = "1.0",
                description = "API REST para planificar rutas de senderismo, cruzando dificultad de la ruta, " +
                        "pronostico del clima e historial del usuario en una recomendacion (RECOMENDADO / " +
                        "PRECAUCION / NO_RECOMENDADO)."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SenderosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SenderosApiApplication.class, args);
	}

}
