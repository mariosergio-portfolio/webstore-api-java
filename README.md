# Webstore REST API (Java / Spring)

Web Store REST API — **Catalog module** (Products + Categories), built with **Java 25 / Spring Boot 4.1** following **Hexagonal Architecture (Ports & Adapters)**.

- Group/Artifact: `com.mycompany` / `webstore-java`
- Base package: `com.mycompany.webstore`
- GitHub: https://github.com/mariosergio30/webstore-java-api

---

Swagger UI: `http://localhost:8080/swagger-ui.html`

Reports module (jOOQ + PostGIS, including `GET /api/report/cities/nearby`): see [README-report-postGIS.md](README-report-postGIS.md).

---

## Architecture: Hexagonal (Ports & Adapters)

Requests flow **in** through a driving adapter (REST controller), cross an **input port**
into the application core, and any persistence need flows **out** through an **output port**
to a driven adapter (JPA). The domain model at the center has zero framework dependencies —
no Spring, no JPA, no Lombok.

```
                              ┌─────────────────────────────┐
                              │      HTTP client / caller    │
                              └───────────────┬───────────────┘
                                              │ JSON
                                              ▼
                              ┌─────────────────────────────┐
   DRIVING ADAPTER            │      ProductController       │  infrastructure/rest/catalog
   (infrastructure/rest)      │  (ProductRequest/Response,   │
                              │   ProductRestMapper)         │
                              └───────────────┬───────────────┘
                                              │ implements
                                              ▼
   INPUT PORT                  ┌─────────────────────────────┐
   (application/port/in)       │      ProductPort  (interface)│  application/port/in
                              └───────────────┬───────────────┘
                                              │
                                              ▼
   APPLICATION SERVICE         ┌─────────────────────────────┐
   (application/service)       │      ProductPortImpl         │  business rules,
                              │  (use cases, validations)    │  UUIDs, timestamps
                              └───────────────┬───────────────┘
                                              │ depends on
                                              ▼
   DOMAIN MODEL                 ┌─────────────────────────────┐
   (domain/model)              │   Product / Category          │  plain Java,
                              │   (archive(), isActive()...)  │  no framework deps
                              └───────────────┬───────────────┘
                                              │ persisted via
                                              ▼
   OUTPUT PORT                  ┌─────────────────────────────┐
   (application/port/out)       │   ProductRepository (interface)│ application/port/out
                              └───────────────┬───────────────┘
                                              │ implements
                                              ▼
   DRIVEN ADAPTER                ┌─────────────────────────────┐
   (infrastructure/persistence) │ ProductPersistenceAdapterImpl │
                              │ (ProductPersistenceMapper)    │
                              └───────────────┬───────────────┘
                                              │ Domain ↔ JPA entity
                                              ▼
                              ┌─────────────────────────────┐
                              │  ProductJpaRepository          │  Spring Data JPA
                              │  → ProductJpaEntity → DB       │  (H2 dev / PostgreSQL prod)
                              └─────────────────────────────┘
```

**Rules enforced across the codebase:**

- Domain objects (`domain/model/`) never depend on Spring, JPA, or Lombok.
- Application services depend only on port interfaces and domain objects — never on JPA entities or HTTP types.
- JPA entities live exclusively in `infrastructure/persistence/entity/` and never cross into domain/application.
- A `RestMapper` and a `PersistenceMapper` (both MapStruct, `componentModel = "spring"`) sit at each boundary.
- Controllers inject the **input port interface**, never the service implementation directly.

---

### REST API
![img.png](doc/swagger-api-img.png)

---
Base path: `/api`

| Method | Path | Description |
|---|---|---|
| GET | `/api/products` | List/search (`q`, `categoryId`, `minPrice`, `maxPrice`, `status`, pagination) |
| GET | `/api/products/{id}` | Get by UUID |
| POST | `/api/products` | Create (status → DRAFT) |
| PUT | `/api/products/{id}` | Full update (SKU immutable) |
| PATCH | `/api/products/{id}` | Partial update (null fields skipped) |
| DELETE | `/api/products/{id}` | Archive (soft-delete) |
| GET/POST/PUT | `/api/categories[/{id}]` | Category CRUD |
| DELETE | `/api/categories/{id}` | Hard delete (blocked if products reference it) |


### Key libraries

| Library | Version | Purpose |
|---|---|---|
| Spring Boot | 4.1.0 | Framework |
| Spring Actuator | Spring Boot managed | Health, info, metrics, loggers, mappings endpoints |
| MapStruct | 1.6.3 | Compile-time object mapping at layer boundaries |
| Lombok | Spring Boot managed | `provided` scope only — used in infra layer, never in domain |
| SpringDoc OpenAPI | 2.8.9 | Swagger UI at `/swagger-ui.html`, API docs at `/v3/api-docs` |
| JJWT | 0.13.0 | JWT dependency present — not yet wired into security filter |
| Flyway | Spring Boot managed | DB migrations (prod only) |
| H2 | Spring Boot managed | In-memory DB for dev |
| PostgreSQL driver | Spring Boot managed | Production DB |
| Testcontainers BOM | 2.0.5 | Integration test containers (future use) |

### Naming conventions

| Layer | Pattern | Example |
|---|---|---|
| Input port | `XxxPort` | `ProductPort` |
| Output port | `XxxRepository` | `ProductRepository` |
| Service | `XxxPortImpl` | `ProductPortImpl` |
| Persistence adapter | `XxxPersistenceAdapterImpl` | `ProductPersistenceAdapterImpl` |
| JPA entity | `XxxJpaEntity` | `ProductJpaEntity` |
| JPA repository | `XxxJpaRepository` | `ProductJpaRepository` |
| REST mapper | `XxxRestMapper` | `ProductRestMapper` |
| Persistence mapper | `XxxPersistenceMapper` | `ProductPersistenceMapper` |

### Exception handling (RFC 9457 `ProblemDetail`)

| Exception | HTTP Status | Use when |
|---|---|---|
| `ResourceNotFoundException` | 404 | Entity not found by ID or SKU |
| `BusinessRuleException` | 422 | Domain rule violated (duplicate SKU, price ≤ 0, self-parent, etc.) |
| `MethodArgumentNotValidException` | 400 | Jakarta Bean Validation failed |


## Deploy on AWS

This repository contains only the application. For more details on how to deploy it on AWS
(CloudFormation + ECS), see: https://github.com/mariosergio-portfolio/iac-aws-cloud-formation/

### Why `buildspec.yml`?

`buildspec.yml` (at the repository root) is the build script that **AWS CodeBuild** reads to know
how to build this application. It lives with the application code because the build steps
depend on the code (the `Dockerfile`), while the infrastructure repository only provisions the
CodeBuild project that runs it. CodeBuild looks for this file automatically, so without it the
build has no instructions. It:

1. Logs in to Amazon ECR.
2. Builds the Docker image from the `Dockerfile`.
3. Pushes the image to ECR, tagged `${ECR_REPO}:${IMAGE_TAG}`.
4. Writes `imageDetail.json` (the pushed image URI) as a build artifact for downstream deploy steps.

The values `AWS_DEFAULT_REGION`, `AWS_ACCOUNT_ID`, `ECR_REPO` and `IMAGE_TAG` are supplied as
environment variables by the CodeBuild project (`ECR_REPO` and `IMAGE_TAG` have defaults in the file).

## Configuration (`.env.example`)

The application reads its database connection and public URL from environment variables, so no
credentials are stored in the repository. `.env.example` is the template listing them:

| Variable | Purpose |
|---|---|
| `DB_HOST`, `DB_PORT` (default `5432`), `DB_NAME` | PostgreSQL / Aurora PostgreSQL location |
| `DB_USERNAME`, `DB_PASSWORD` | Database credentials |
| `APP_PUBLIC_URL` | Public URL exposed by the app (`app.public.url`, defaults to `http://localhost:8080/`) |

To use it, copy `.env.example` to `.env` and fill in the values. `.env` is git-ignored — never
commit real credentials. On AWS the same variables are provided by the ECS task definition
instead (see the infrastructure repository above).

## Local Quick start

```bash
# Build (skip tests)
mvn -B package -DskipTests

# Run tests
mvn test

# Run the application (dev profile — H2 in-memory, no Flyway)
mvn spring-boot:run
```