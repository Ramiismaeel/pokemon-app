# Pokédex Service

A Spring Boot REST API that fetches Pokémon data from the [PokéAPI](https://pokeapi.co/) and manages a personal favorites collection stored in MongoDB.

Built as a training project covering:

- `RestClient` for calling an external API
- DTOs (separating the external API model from our own API)
- Spring Validation
- Global exception handling with `@RestControllerAdvice`
- CRUD endpoints
- Spring Data MongoDB

---

## Tech stack

| | |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1 (Spring Framework 7) |
| Database | MongoDB (Spring Data MongoDB) |
| HTTP client | Spring `RestClient` |
| Validation | Jakarta Bean Validation (Hibernate Validator) |
| Build | Maven |
| Tests | JUnit 6, Mockito, MockMvc, `MockRestServiceServer`, embedded MongoDB (Flapdoodle) |

---

## Getting started

### Prerequisites

- JDK 25
- Maven (or the included Maven wrapper)
- A running MongoDB instance (default: `mongodb://localhost:27017`)

Start MongoDB quickly with Docker:

```bash
docker run -d --name pokemon-mongo -p 27017:27017 mongo
```

### Configuration

`src/main/resources/application.properties`:

```properties
spring.application.name=pokemon-app
spring.mongodb.uri=mongodb://localhost:27017/pokemon-app
```

### Run

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

### Test

```bash
./mvnw test
```

Tests do **not** need a running MongoDB or internet access: an embedded MongoDB is started automatically, and PokéAPI calls are mocked.

---

## API

### Pokémon lookup

#### `GET /api/pokemon/{name}`

Fetches a Pokémon from PokéAPI and returns a simplified version. Nothing is stored.

```bash
curl http://localhost:8080/api/pokemon/pikachu
```

```json
{
  "pokemonId": "25",
  "pokemonName": "pikachu",
  "pictureUrl": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
  "height": 4,
  "weight": 60,
  "types": ["electric"]
}
```

| Status | When |
|---|---|
| `200 OK` | Pokémon found |
| `404 Not Found` | PokéAPI does not know the Pokémon |
| `502 Bad Gateway` | PokéAPI returned an empty or unusable response |

---

### Favorites collection

All read, update and delete operations use **only the local database**. PokéAPI is called only when a favorite is created.

#### `POST /api/collection` – add a favorite

```json
{
  "pokemonName": "pikachu",
  "nickname": "My Starter"
}
```

Flow:

1. Validate the request
2. Look up the Pokémon in PokéAPI (fails with `404` if it does not exist)
3. Combine the PokéAPI data, the nickname and a newly generated UUID
4. Save the favorite in MongoDB

Response:

```json
{
  "id": "4b903fd9-93df-4fd9-a4fc-1b89b7d1b0f5",
  "pokemonId": "25",
  "pokemonName": "pikachu",
  "nickname": "My Starter",
  "pictureUrl": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
  "height": 4,
  "weight": 60,
  "types": ["electric"]
}
```

#### `GET /api/collection` – all favorites

Returns a list of all saved favorites (empty list if none).

#### `GET /api/collection/{id}` – one favorite

Returns the favorite with the given UUID, or `404` if it does not exist.

#### `PUT /api/collection/{id}` – update nickname

```json
{
  "nickname": "Arena Champion"
}
```

Only the nickname changes. All Pokémon data (`pokemonId`, name, picture, height, weight, types) stays untouched.

#### `DELETE /api/collection/{id}` – remove a favorite

Deletes the favorite, or returns `404` if the ID does not exist.

#### Status codes

| Status | When |
|---|---|
| `200 OK` | Success |
| `400 Bad Request` | Validation failed or malformed request |
| `404 Not Found` | Unknown favorite ID, or the Pokémon does not exist in PokéAPI (POST) |
| `502 Bad Gateway` | PokéAPI failed (POST) |

---

## Validation

| Field | Rules |
|---|---|
| `pokemonName` | required, not blank |
| `nickname` | required, not blank, at least 3 characters |

Examples of rejected requests:

```json
{ "pokemonName": "", "nickname": "" }
```

```json
{ "pokemonName": "pikachu", "nickname": "A" }
```

---

## Error responses

All errors are handled centrally in `GlobalExceptionHandler` and share one format:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Pokemon: notapokemon is not found",
  "timestamp": "2026-10-21T10:15:00Z",
  "details": {}
}
```

For validation errors, `details` lists every message per field (a field can fail several rules at once):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2026-10-21T10:15:00Z",
  "details": {
    "nickname": ["must not be blank", "it should be 3 characters at least"],
    "pokemonName": ["must not be blank"]
  }
}
```

| Exception | Status |
|---|---|
| `NotFoundException` (base of `PokemonNotFoundException`, `FavoriteNotFoundException`) | `404` |
| `PokemonApiUnavailableException` | `502` |
| `MethodArgumentNotValidException` (invalid request body) | `400` |
| `HandlerMethodValidationException` (invalid path variable) | `400` |

---

## Project structure

```
src/main/java/training/pokemonapp
├── controller
│   ├── PokemonController        GET /api/pokemon/{name}
│   └── FavoriteController       CRUD on /api/collection
├── service
│   ├── PokemonService           PokéAPI access + mapping to PokemonDto
│   ├── FavoriteService          favorites logic (uses PokemonService + repository)
│   └── IdService                UUID generation (mockable in tests)
├── dto
│   ├── PokemonResponse, Sprites, Other, OfficialArtwork, TypeSlot, NamedResource
│   │                            mapping of the external PokéAPI response
│   ├── PokemonDto               simplified Pokémon returned to clients
│   ├── CreateFavoriteRequest    POST body (pokemonName + nickname)
│   └── UpdateFavoriteRequest    PUT body (nickname)
├── model
│   └── Favorite                 MongoDB document
├── repo
│   └── FavoriteRepository       Spring Data MongoRepository
└── exception
    ├── NotFoundException        abstract base for all "not found" cases
    ├── PokemonNotFoundException
    ├── FavoriteNotFoundException
    ├── PokemonApiUnavailableException
    ├── ErrorResponse
    └── GlobalExceptionHandler
```

### DTO separation

The project deliberately keeps two kinds of DTOs apart:

- **PokéAPI DTOs** mirror only the parts of PokéAPI's large, nested JSON that we need (for example `sprites.other["official-artwork"].front_default`). Everything else is ignored.
- **Own DTOs** define the request and response shapes of this API. `CreateFavoriteRequest` only contains `pokemonName` and `nickname`. The service enriches it with PokéAPI data and a UUID before saving it as a `Favorite`.

If PokéAPI changes, only the PokéAPI DTOs and the mapping in `PokemonService` need updating.

### Request flow (POST)

```
Client ──POST──▶ FavoriteController ──▶ FavoriteService ──▶ PokemonService ──▶ PokéAPI
                                              │
                                              ├──▶ IdService (UUID)
                                              └──▶ FavoriteRepository ──▶ MongoDB
```

---

## Tests

| Test class | Type | What it covers |
|---|---|---|
| `PokemonServiceTest` | Unit (Mockito) | Mapping PokéAPI response → `PokemonDto`, null handling |
| `FavoriteServiceTest` | Unit (Mockito) | Create, read, update, delete, not-found cases; no PokéAPI calls on reads |
| `PokemonControllerTest` | Integration (`@SpringBootTest`, MockMvc, `MockRestServiceServer`) | `GET /api/pokemon/{name}` end to end |
| `FavoriteControllerTest` | Integration (`@SpringBootTest`, MockMvc, embedded MongoDB) | All `/api/collection` endpoints, validation, `404` cases, database state |
