# Service Directory API

Backend for an enterprise service directory. It tracks **teams** and the **services** they own, so people can find who owns what, where it runs, and where its code lives.

> Status: work in progress (Day 1 of 4). Core CRUD is done. Validation, error handling, search and tests are coming.

## Tech stack

- Java 25, Spring Boot 4.1.1 (Maven)
- Spring Data JPA (Hibernate 7), PostgreSQL 16
- Flyway migrations, Bean Validation, Lombok
- springdoc-openapi 3.1.1 (Swagger UI)
- Docker Compose for the database

## Prerequisites

- JDK 25
- Docker Desktop (running)

## Run locally

```bash
git clone https://github.com/SMishra0209/service-directory.git
cd enterprise-service-directory
docker compose up -d
./mvnw spring-boot:run
```

On Windows PowerShell use `.\mvnw spring-boot:run`.

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

The database runs on **host port 5433** (not the default 5432), so it doesn't clash with a PostgreSQL already installed on your machine. Defaults are set in `application.yml` and can be overridden with the environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` and `DB_PASSWORD`.

## API

Base path: `/api/v1`

| Method | Path | Description |
|---|---|---|
| GET | `/teams` | List teams |
| GET | `/teams/{id}` | Get one team |
| POST | `/teams` | Create a team |
| PUT | `/teams/{id}` | Replace a team |
| DELETE | `/teams/{id}` | Delete a team |
| GET | `/services` | List services |
| GET | `/services/{id}` | Get one service |
| POST | `/services` | Create a service |
| PUT | `/services/{id}` | Replace a service |
| DELETE | `/services/{id}` | Delete a service |

## Data model

- **team**: `id`, `name` (unique), `contact_email`
- **service_entry**: `id`, `name` (unique), `description`, `status` (ACTIVE, DEPRECATED, RETIRED), `environment` (DEV, STAGING, PROD), `base_url`, `repo_url`, `team_id` (required, references team), `created_at`, `updated_at`

Many services belong to one team. The schema is created only by Flyway migrations (`src/main/resources/db/migration`); Hibernate's `ddl-auto` is `none`. Migrations are append-only: existing files are never edited, only new versions are added.

## Design

Layered architecture, packaged by feature: `common`, `team`, `serviceentry`. Controllers talk to services, services talk to repositories. API input and output use separate DTO records, not entities.

## Assumptions

The task gave no detailed requirements, so I assumed:

- Names of teams and services are **globally unique**, enforced by the database.
- Every service belongs to exactly one existing team.
- A team that still owns services cannot be deleted (foreign key).
- `base_url` and `repo_url` are optional, but must be valid URLs when given.
- Timestamps are stored in UTC, and the app runs in UTC.
- PUT replaces the whole resource, so all required fields must be sent.
- Authentication was not specified. It is planned as an optional extra at the end.

## Roadmap

- [x] Project setup, Docker Postgres, Flyway schema, Swagger
- [x] Team and ServiceEntry CRUD
- [ ] Validation messages and global error handling (400, 404, 409)
- [ ] Search, filtering and pagination
- [ ] Tests (Testcontainers)
- [ ] Dockerfile and full Docker Compose
- [ ] Authentication (JWT, ADMIN and VIEWER roles), time-boxed
- [ ] Seed data