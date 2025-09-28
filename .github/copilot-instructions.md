# Copilot instructions for customer-identity

Short, actionable guidance for automated code agents working in this repo.

1. Project purpose and layout
   - This is a small Spring Boot REST API (single module Maven project). Entry point: `CustomerIdentityApplication`.
   - Main packages:
     - `controller` — HTTP endpoints and global exception handler (`CustomerController`, `GlobalExceptionHandler`).
     - `service` — business logic (`CustomerService`). Note: service class currently lives under `controller` source folder but its package is `com.keviny.customeridentity.service`.
     - `repository` — Spring Data JPA repositories (`CustomerRepository`).
     - `model` / `dto` / `mapper` — JPA entities, data transfer objects, and manual mapping (`CustomerIdentity`, `CustomerDto`, `CustomerMapper`).

2. Key patterns and conventions
   - Controller -> Service -> Repository pattern. Controllers should be thin and delegate to `CustomerService` for business rules.
   - DTOs are used for request/response validation. Validation annotations (Jakarta) are present on `CustomerDto`. Global validation errors are handled by `GlobalExceptionHandler` which returns `ErrorResponse`.
   - Mapping uses `CustomerMapper` static helpers. Note: mapper intentionally does NOT copy ID to entity and only sets SSN on new entities (entity.getId() == null).
   - Unique identity uses `ssn` as a unique field (`CustomerRepository.findBySsn(String)`). Use this method when deduplicating or upserting.
   - Persistence: standard Spring Data JPA + entity annotations; DB schema (table `customer_identity`) expected to exist or be created via JPA.

3. Build / run / test
   - Build: standard Maven at repo root. Example: `mvn -e -DskipTests package` (Windows PowerShell: `mvn -e -DskipTests package`).
   - Run locally: `java -jar target/customer-identity-0.0.1-SNAPSHOT.jar` after building.
   - No tests included. If adding tests, follow package layout and use Spring Boot test slice annotations where appropriate.

4. Files to check before edits
   - `CustomerController.java` — endpoints are `/api/customers` (GET by id, POST create/update). Keep signature and validation annotations intact.
   - `CustomerService.java` — contains create/update behavior using `findBySsn(...)` then mapping with `CustomerMapper` and `customerRepository.save(...)`.
   - `CustomerMapper.java` — manual mapping rules: do not set ID on entity; only set SSN for new entities.
   - `CustomerIdentity` — JPA annotations and column lengths; SSN is unique and limited to 32 chars.
   - `GlobalExceptionHandler.java` and `ErrorResponse.java` — how validation errors are returned (400 + structured error map). Mirror this format when adding new validation checks.

5. Safety and backward-compatibility rules for agents
   - Do not change primary key strategy or alter `ssn` uniqueness without updating repository and tests.
   - Preserve validation annotations on `CustomerDto` and shape of `ErrorResponse` to avoid breaking API clients.
   - When adding fields to DTO/entity: update `CustomerMapper` and `CustomerRepository` queries that rely on SSN.

6. Common TODOs and low-risk improvements
   - Move `CustomerService.java` file into `service` directory (source is currently under `controller` folder) to match package path — do this carefully to avoid breaking builds.
   - Add unit tests for `CustomerService` (mock `CustomerRepository`) and controller integration tests.
   - Add README with run/dev notes if missing.

7. Examples
   - Upsert flow: POST `/api/customers` with JSON body matching `CustomerDto` -> `CustomerService.createOrUpdateCustomer` uses `findBySsn` -> `CustomerMapper.toEntity` -> `customerRepository.save` -> returns 201 with Location `/api/customers/{id}`.

If any part of the codebase or runtime commands are unclear, ask for permission to run a quick build or add tests. When done, request a review from the repo owner for behavioral changes.