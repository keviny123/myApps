# dev.ps1 - start Postgres, build and run the app (PowerShell)

param(
    [switch]$Rebuild
)

Write-Output "Starting dev Postgres using docker-compose..."
docker-compose up -d db

if ($Rebuild) {
    Write-Output "Building project (skipping tests)..."
    mvn -e -DskipTests package
}

Write-Output "Running jar..."
Start-Process -NoNewWindow -FilePath "java" -ArgumentList "-jar","target/customer-identity-0.0.1-SNAPSHOT.jar"

Write-Output "Application started (in background). To view logs: docker-compose logs -f db ; or view the java process stdout in your terminal if you didn't use Start-Process."