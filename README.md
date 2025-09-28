# customer-identity

Small Spring Boot REST service that stores simple customer identity data.

![CI](https://github.com/keviny123/myApps/actions/workflows/maven-ci.yml/badge.svg)

Quick start (build & run)

1. Build with Maven (Windows PowerShell):

```powershell
mvn -e -DskipTests package
```

2. Run the fat jar:

```powershell
java -jar target/customer-identity-0.0.1-SNAPSHOT.jar
```

API endpoints

- GET /api/customers/{id} — returns `CustomerDto` or 404
- POST /api/customers — creates or updates a customer by SSN (upsert). Expects JSON body matching `CustomerDto` with validation.

Example POST body (JSON):

```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "gender": "F",
  "dob": "1990-01-01",
  "ssn": "111-22-3333"
}
```

Notes

- The project uses Spring Data JPA and expects a datasource configured via application properties or environment variables. No database migrations are included — ensure a database is available or enable an in-memory DB for testing.
- `ssn` is treated as a unique identifier and used by the service to upsert records.
- Validation errors return HTTP 400 with a JSON `ErrorResponse` (see `controller/GlobalExceptionHandler.java` and `dto/ErrorResponse.java`).
- Developer guidance for automated agents is available in `.github/copilot-instructions.md`.

Files of interest

- `src/main/java/com/keviny/customeridentity/controller/CustomerController.java`
- `src/main/java/com/keviny/customeridentity/service/CustomerService.java` (note: currently located under `controller` source folder)
- `src/main/java/com/keviny/customeridentity/repository/CustomerRepository.java`
- `src/main/java/com/keviny/customeridentity/dto/CustomerDto.java`
- `src/main/java/com/keviny/customeridentity/mapper/CustomerMapper.java`

If you'd like, I can add a small `docker-compose.yml` for a quick Postgres dev DB or add a unit test scaffold next.

Profiles and local dev

This project includes a `dev` Spring profile that expects a Postgres database. The default
connection values are read from environment variables: `JDBC_URL`, `JDBC_USER`, and
`JDBC_PASSWORD`. For local development you can run the bundled `docker-compose.yml` and
start the app normally:

```powershell
docker-compose up -d db
mvn -e -DskipTests package
java -jar target/customer-identity-0.0.1-SNAPSHOT.jar
```

For CI the workflow runs two variants: `test` (H2 in-memory) and `dev` (Postgres service).