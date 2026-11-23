# Betriebskostenabrechnung

Jährliche Betriebskostenabrechnung für die eigenen Mieter. Version 0.1.0.

Die Fachsprache steht in `docs/domain-model.md`, der technische Rahmen in `docs/technische-rahmenbedingungen.md`. Die Änderungen stehen in `RELEASE_NOTES.md`.

## Start

Aus `backend/`:

```text
./mvnw quarkus:dev
```

Der Dev-Server hört auf Port 29215. Oberfläche und API liegen unter `http://localhost:29215/betriebskostenabrechnung/`. Der erste Start baut das Frontend mit npm. Die laufende Oberfläche ist dieser Build. Für Hot-Reload zusätzlich in `frontend/` `npm run dev` starten und `http://localhost:5173/betriebskostenabrechnung/` öffnen.

## Anmeldung

Platzhalter in `backend/src/main/resources/application.yml`:

- Benutzername: `eigentuemer`
- Passwort: `bitte-aendern`

## Stack

- Oberfläche: React, TypeScript, PrimeReact Community, Vite, Node 24
- Backend: Quarkus, Java 25, Maven 3.9
- Daten: H2-Datei, Flyway, Hibernate, Blaze Persistence
- API: REST mit OpenAPI
