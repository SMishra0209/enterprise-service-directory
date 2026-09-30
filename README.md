# Service Directory API

Backend for an enterprise service directory. It tracks **teams** and the **services** they own, so people can find who owns what, where it runs, and where its code lives.

> Status: Day 3 of 4. Core CRUD, validation, global error handling, search/filtering/pagination, integration tests, and full Docker Compose (app + db) are done. Authentication is planned as a time-boxed extra on Day 4.

## Tech stack

- Java 25, Spring Boot 4.1.1 (Maven)
- Spring Data JPA (Hibernate 7), PostgreSQL 16
- Flyway migrations, Bean Validation, Lombok
- springdoc-openapi 3.1.1 (Swagger UI)
- JUnit 5 + Testcontainers (integration tests against a real Postgres container)
- Docker Compose for the full stack (app + database)

## Prerequisites

**To run with Docker (recommended, no local Java/Maven setup needed):**
- Docker Desktop (running)

**To run or develop locally outside Docker:**
- JDK 25
- Docker Desktop (running, for the database)

## Run with Docker (full stack)

This builds and runs both the app and the database in containers — the quickest way to get the API running.

```bash
git clone https://github.com/SMishra0209/enterprise-service-directory.git
cd enterprise-service-directory
docker compose up --build
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- API base: http://localhost:8080/api/v1

Stop with `docker compose down` (add `-v` to also delete the database volume and start fresh next time).

## Run locally (for development)

Runs the app directly (e.g. from an IDE) against a database in Docker.

```bash
git clone https://github.com/SMishra0209/enterprise-service-directory.git
cd enterprise-service-directory
docker compose up -d db
./mvnw spring-boot:run
```

On Windows PowerShell use `.\mvnw spring-boot:run`.

The database runs on **host port 5433** (not the default 5432), so it doesn't clash with a PostgreSQL already installed on your machine. Defaults are set in `application.yml` and can be overridden with the environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` and `DB_PASSWORD`. Inside Docker Compose, the `app` container reaches the database at `db:5432` — the internal service name and port — while `localhost:5433` is only for connecting from your host machine (e.g. from an IDE or a DB client).

## Run the tests

Integration tests use Testcontainers, which starts a real, throwaway Postgres container for the test run — Docker Desktop must be running.

```bash
./mvnw clean install
```

## API

Base path: `/api/v1`

| Method | Path | Description |
|---|---|---|
| GET | `/teams` | List teams |
| GET | `/teams/{id}` | Get one team |
| POST | `/teams` | Create a team |
| PUT | `/teams/{id}` | Replace a team |
| DELETE | `/teams/{id}` | Delete a team |
| GET | `/services` | List services (supports filtering and pagination, see below) |
| GET | `/services/{id}` | Get one service |
| POST | `/services` | Create a service |
| PUT | `/services/{id}` | Replace a service |
| DELETE | `/services/{id}` | Delete a service |

### Filtering and pagination

`GET /services` accepts optional query parameters, combinable in any mix:

| Param | Description |
|---|---|
| `name` | Partial, case-insensitive match on service name |
| `status` | Exact match: `ACTIVE`, `DEPRECATED`, or `RETIRED` |
| `environment` | Exact match: `DEV`, `STAGING`, or `PROD` |
| `teamId` | Services belonging to this team |
| `page`, `size` | Pagination (defaults: page 0, size 20) |
| `sort` | e.g. `sort=name,desc` |

The response is a Spring `Page` object (`content`, `totalElements`, `totalPages`, `number`, `size`, etc.), not a bare array.

Example: `GET /services?status=ACTIVE&environment=PROD&page=0&size=10`

### Error responses

All errors share one consistent JSON shape:

```json
{
  "timestamp": "2026-09-29T08:16:52.404Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields.",
  "path": "/api/v1/teams",
  "fieldErrors": [
    { "field": "name", "message": "must not be blank" }
  ]
}
```

| Status | When |
|---|---|
| 400 | Validation failure, malformed JSON body, or invalid enum/type in the request |
| 404 | Resource (team or service) not found |
| 409 | Duplicate name, or deleting a team that still owns services |

`fieldErrors` is only present for validation failures; otherwise it's omitted.

## Data model

- **team**: `id`, `name` (unique), `contact_email`
- **service_entry**: `id`, `name` (unique), `description`, `status` (ACTIVE, DEPRECATED, RETIRED), `environment` (DEV, STAGING, PROD), `base_url`, `repo_url`, `team_id` (required, references team), `created_at`, `updated_at`

Many services belong to one team. The schema is created only by Flyway migrations (`src/main/resources/db/migration`); Hibernate's `ddl-auto` is `none`. Migrations are append-only: existing files are never edited, only new versions are added.

## Design

Layered architecture, packaged by feature: `common`, `team`, `serviceentry`. Controllers talk to services, services talk to repositories. API input and output use separate DTO records, not entities. Filtering uses Spring Data `Specification`s (`common`/`serviceentry` package) so filters combine freely without a separate query method