Title: feat: infra + migrations + CI matrix + dev script

High-level summary
- Adds Flyway-based DB migrations and a V1 migration to create the `customer_identity` table.
- Adds local dev helpers (docker-compose and `dev.ps1`) to run a Postgres dev DB and start the app.
- Adds a `dev` Spring profile (Postgres) plus an H2 test configuration for fast in-memory tests.
- Converts CI to a matrix that runs tests against both H2 (fast) and Postgres (dev profile).
- Adds a GitHub Actions badge to the `README.md`.

Files changed / added (high level)
- `pom.xml`: add H2 (test) and `flyway-core` dependency.
- `docker-compose.yml`: Postgres dev service.
- `dev.ps1`: PowerShell helper to start Postgres, build and run the jar (Windows convenience).
- `src/main/resources/application.yml`: `dev` profile wiring for Postgres (env-driven).
- `src/test/resources/application-test.yml`: H2 in-memory config for tests.
- `src/main/resources/db/migration/V1__create_customer_identity_table.sql`: Flyway migration (creates table).
- `.github/workflows/maven-ci.yml`: CI matrix (H2, Postgres) and runs `mvn test`.
- Tests added: `CustomerServiceTest`, `CustomerControllerTest` (unit / slice tests).

Why this change
- Ensures DB schema is managed with migrations (Flyway) so CI/dev parity improves.
- Provides a stable, repeatable dev environment via docker-compose and a short PowerShell script.
- Improves CI coverage by running tests in two configurations: in-memory H2 and real Postgres.

How to run locally (dev)
1. Start Postgres for local dev (background):

```powershell
docker-compose up -d db
```

2. Build and run (Windows PowerShell):

```powershell
mvn -e -DskipTests package
java -jar target/customer-identity-0.0.1-SNAPSHOT.jar
```

3. (Optional) Use the convenience script to start DB, build and run:

```powershell
.\dev.ps1 -Rebuild
```

Notes about migrations and profiles
- Flyway will automatically pick up migrations in `classpath:db/migration` and apply them at startup when a DB connection is available (default Flyway Spring Boot behavior).
- The `dev` Spring profile uses Postgres connection details from environment variables: `JDBC_URL`, `JDBC_USER`, `JDBC_PASSWORD`. Defaults are provided in `application.yml` for local docker-compose usage.
- Tests use `application-test.yml` which configures an H2 in-memory datasource. CI runs both `test` (H2) and `dev` (Postgres) matrix entries.

Testing performed / verification
- Locally ran `mvn test` — all unit and slice tests passed (3 tests in total at time of commit).
- Verified commit and push of `feat/infra-migrations-ci` branch. The branch is available for PR creation.

Reviewer checklist
- [ ] Confirm Flyway migration SQL matches schema expectations (column types, lengths, unique constraint on `ssn`).
- [ ] Validate CI matrix configuration and whether you want Postgres integration tests to run on every push (cost/time tradeoff).
- [ ] Confirm `dev.ps1` is acceptable as a Windows helper (non-Windows contributors can use `docker-compose` manually).

Follow-ups (optional)
- Add Flyway baseline or additional migrations for incremental schema evolution.
- Add a small integration test suite that runs against Postgres service in CI (smoke tests to validate migrations + schema).
- Add a GitHub PR template and expand `README.md` with more detailed dev onboarding steps.

If you want I can open the PR on GitHub for you (branch `feat/infra-migrations-ci` -> `main`) or prepare the PR description for `gh pr create` — tell me which you prefer.
