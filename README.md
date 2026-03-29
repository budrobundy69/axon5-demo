# Axon Framework 5 Demo (Java 26, Spring Boot 4.0.5)

Dieses Projekt zeigt ein minimales CQRS/Event-Driven Beispiel mit:

-   Axon Framework 5.0.4
-   Spring Boot 4.0.5
-   Java 26
-   Persistenter Event Store und Token Store via JPA

## Starten

### Default (lokal, persistentes H2 File)

```bash
./mvnw spring-boot:run
```

Die Daten liegen dann in `./.data/axon-demo`.

### PostgreSQL Profil

Postgres starten (mit `docker-compose.yml`):

```bash
docker compose up -d postgres
```

App mit Postgres-Profil starten:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Optional können folgende Variablen gesetzt werden:

-   `POSTGRES_HOST` (default: `localhost`)
-   `POSTGRES_PORT` (default: `5432`)
-   `POSTGRES_DB` (default: `axon_demo`)
-   `POSTGRES_USER` (default: `postgres`)
-   `POSTGRES_PASSWORD` (default: `postgres`)

## API testen (pwsh)

### 1) Konto eröffnen

```pwsh
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/accounts" `
  -ContentType "application/json" `
  -Body '{"accountId":"acc-1","initialBalance":1000}'
```

### 2) Einzahlung buchen

```pwsh
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/accounts/acc-1/deposits" `
  -ContentType "application/json" `
  -Body '{"amount":250}'
```

### 3) Einzelnes Konto lesen

```pwsh
Invoke-RestMethod -Method Get -Uri "http://localhost:8081/accounts/acc-1"
```

### 4) Alle Konten lesen

```pwsh
Invoke-RestMethod -Method Get -Uri "http://localhost:8081/accounts"
```
