Release notes — customer-identity
=================================

Release date: 2025-09-28

Summary
-------
This release includes infrastructure, CI, and database migration improvements for the `customer-identity` service. Changes were implemented on branch `feat/infra-migrations-ci` and merged into `main` via PRs #1 and #2.

Key changes
-----------
- Add Flyway DB migration: `V1__create_customer_identity_table.sql` to create `customer_identity` table.
- Add GitHub Actions CI workflow with a matrix: runs against H2 (fast unit tests) and Postgres (integration verification).
- Add `docker-compose.yml` for a local Postgres development database and a `dev.ps1` helper script for Windows convenience.
- Add `application-test.yml` for H2-based tests and update `application.yml` dev profile activation.
- Add unit and controller tests (JUnit 5, Mockito, Spring Boot Test slice) and H2 test configuration.
- Add `.github/copilot-instructions.md`, `README.md`, `.gitattributes`, and `.gitignore` for better developer experience and contributor guidance.

Files of note
-------------
- `src/main/resources/db/migration/V1__create_customer_identity_table.sql` — Flyway migration
- `.github/workflows/maven-ci.yml` — CI workflow (matrix for H2 and Postgres)
- `docker-compose.yml` — Postgres service for local development
- `dev.ps1` — Windows dev helper script
- `PULL_REQUEST_BODY.md` — prepared PR body used when creating the PR

Follow-ups
----------
- Consider adding an integration test that runs against the Postgres service in CI if you want stronger end-to-end coverage.
- Tag a release and publish release notes in GitHub Releases if you'd like to expose these changes to consumers.

Verification
------------
- Local `mvn test` was executed during development and reported `BUILD SUCCESS` with all tests passing.
