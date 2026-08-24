# Senderos API

API REST para planificar rutas de senderismo, construida con **Spring Boot 3.5** y **Java 21**. Proyecto personal de aprendizaje enfocado en resolver un problema real: **¿es buena idea hacer esta ruta hoy, dado el clima y mi propio historial?** — cruzando la dificultad objetiva de la ruta, el pronostico del dia elegido y el historial del usuario en una recomendacion, en vez de ser un simple CRUD de rutas.

## Contenido

- [Stack tecnico](#stack-tecnico)
- [Arquitectura](#arquitectura)
- [El feature principal: recomendacion](#el-feature-principal-recomendacion)
- [Seguridad](#seguridad)
- [Como correrlo localmente](#como-correrlo-localmente)
- [Documentacion de la API](#documentacion-de-la-api)
- [Endpoints principales](#endpoints-principales)
- [Tests](#tests)

## Stack tecnico

- **Java 21** + **Spring Boot 3.5** (Gradle)
- **Spring Data JPA** + **PostgreSQL**
- **Flyway** para versionar el esquema de la base de datos (sin `ddl-auto=update`)
- **Spring Security** con autenticacion **JWT** (`io.jsonwebtoken`)
- **RestClient** para consumir una API externa de clima (Open-Meteo), con **`@Cacheable`** para no repetir llamadas
- **Bean Validation** (`spring-boot-starter-validation`)
- **Springdoc OpenAPI / Swagger UI**
- **Lombok**
- **JUnit 5** + **Mockito** + **AssertJ**

## Arquitectura

Estructura en capas, un paquete por responsabilidad:

```
persistence/entity      Entidades JPA (mapeo a la base de datos)
persistence/repository  Interfaces Spring Data
config                  Configuracion de Spring Security, filtro/servicio JWT, RestClient
client                  Cliente HTTP de la API de clima externa (Open-Meteo) y su DTO crudo
service                 Logica de negocio, orquesta los repositorios y clientes
web/controller          Controladores REST (@RestController), bajo /api/...
web/exception           Manejo centralizado de errores (@RestControllerAdvice)
dto                     Records de entrada/salida, desacoplados de las entidades
```

Puntos destacados del diseno:

- Cada controlador expone **DTOs dedicados** (`*CreateDto`, `*UpdateDto`, `*ResponseDto`) en vez de entidades JPA crudas.
- Los errores (recurso no encontrado, coordenadas faltantes, pronostico no disponible, validacion) se resuelven en un unico `GlobalExceptionHandler`.
- El cliente de clima esta separado en 3 piezas con responsabilidad unica: `OpenMeteoResponseDto` (forma cruda de la respuesta externa), `OpenMeteoClient` (la llamada HTTP), `WeatherForecastDto`/`WeatherService` (traduccion a un formato propio, un registro por dia, en español).
- El `userId` de los endpoints de historial y recomendacion **nunca se recibe del cliente**: se extrae del JWT autenticado via `@AuthenticationPrincipal` (`CustomUserDetails`), para que un usuario no pueda consultar o crear historial de otro.

## El feature principal: recomendacion

`GET /api/recommendation?routeId={id}&date={fecha}` cruza tres fuentes de datos ya existentes en una sola respuesta:

1. **Dificultad de la ruta** (`FACIL` / `MODERADA` / `DIFICIL`, enum en base de datos).
2. **Pronostico del dia elegido**, obtenido de `WeatherService` (Open-Meteo).
3. **Historial de rutas del usuario autenticado** (`HikeHistoryService`).

Reglas de advertencia (cada una suma un motivo):

- **Clima riesgoso**: precipitacion >= 10mm, o descripcion "Tormenta"/"Nieve".
- **Falta de experiencia**: la ruta es `DIFICIL` y el usuario nunca completo una ruta `DIFICIL` antes.

El veredicto sale de la cantidad de motivos: `0` -> `RECOMENDADO`, `1` -> `PRECAUCION`, `2` -> `NO_RECOMENDADO`.

## Seguridad

Autenticacion **stateless** basada en JWT (sin sesiones): el cliente hace login/registro una vez y reenvia el token en cada peticion protegida via header `Authorization: Bearer <token>`.

| Ruta | Regla de acceso |
|---|---|
| `POST /api/auth/register`, `POST /api/auth/login` | Publico |
| `/swagger-ui/**`, `/v3/api-docs/**` | Publico |
| Resto de `/api/**` | Requiere estar autenticado |

Un usuario sin token (o con token invalido) recibe `403 Forbidden` en las rutas protegidas. En `/api/hikes/**` y `/api/recommendation`, el usuario relevante siempre es el del token — no existe forma de pasar un `userId` distinto por request.

## Como correrlo localmente

### Requisitos

- Java 21
- PostgreSQL corriendo localmente, con una base de datos llamada `senderos`

### Pasos

```bash
# 1. Clonar el repositorio
git clone <url-del-repo>
cd senderos-api

# 2. Configurar credenciales (DB_USERNAME/DB_PASSWORD son opcionales, caen a postgres/1234 si no se setean;
# JWT_SECRET es obligatorio, la app no arranca sin el)
export DB_USERNAME=postgres
export DB_PASSWORD=1234
export JWT_SECRET=<una-clave-larga-propia>

# 3. Levantar la aplicacion
./gradlew bootRun        # Linux/Mac
.\gradlew.bat bootRun    # Windows PowerShell
```

Flyway aplica las migraciones (`src/main/resources/db/migration`) automaticamente al arrancar — no hace falta correr scripts SQL a mano ni dejar que Hibernate genere el esquema (`ddl-auto=validate`, Flyway es la unica fuente de verdad del esquema).

La API queda disponible en `http://localhost:8080`.

## Documentacion de la API

Con la aplicacion corriendo, la documentacion interactiva (Swagger UI) esta disponible en:

```
http://localhost:8080/swagger-ui/index.html
```

Alli se puede ver cada endpoint con su descripcion, y probarlos directamente: hace login/registro en `Auth`, copia el token, y pegalo con el boton **Authorize** (arriba a la derecha) como `Bearer <token>` para que las siguientes peticiones lo incluyan automaticamente.

## Endpoints principales

### Auth

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/auth/register` | Crea un usuario y devuelve un JWT ya autenticado |
| POST | `/api/auth/login` | Valida credenciales y devuelve un JWT |

### Routes

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/routes` | Lista todas las rutas |
| GET | `/api/routes/{id}` | Obtiene una ruta por id |
| POST | `/api/routes` | Crea una ruta |
| PUT | `/api/routes/{id}` | Actualiza una ruta (parcial) |
| DELETE | `/api/routes/{id}` | Elimina una ruta |

### Weather

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/routes/{routeId}/weather` | Pronostico dia por dia para las coordenadas de la ruta (cacheado) |

### Hike History

| Metodo | Ruta | Descripcion |
|---|---|---|
| POST | `/api/hikes` | Registra una ruta completada para el usuario autenticado |
| GET | `/api/hikes/me` | Historial del usuario autenticado |

### Recommendation

| Metodo | Ruta | Descripcion |
|---|---|---|
| GET | `/api/recommendation?routeId={id}&date={fecha}` | Veredicto RECOMENDADO / PRECAUCION / NO_RECOMENDADO para el usuario autenticado |

## Tests

```bash
./gradlew test
```

Incluye tests unitarios de `RecommendationService` (Mockito + AssertJ) cubriendo los 3 veredictos posibles y las 2 excepciones que puede lanzar (ruta inexistente, pronostico no disponible para la fecha).