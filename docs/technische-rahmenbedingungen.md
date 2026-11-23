# Technische Rahmenbedingungen

Jährliche Betriebskostenabrechnung für die eigenen Mieter. Die Fachsprache steht in `docs/domain-model.md`. Dieses Dokument hält den technischen Rahmen fest.

## Zweck

- Ein Nutzer: der Eigentümer.
- Sprache der Anwendung: Deutsch.
- Bedienung über die Weboberfläche.
- Die Abrechnung wird als PDF ausgegeben.
- Lokal ist die Oberfläche mit Anmeldung erreichbar. Benutzername und Passwort stehen in der Quarkus-Applikationsdatei. Die Sitzung läuft über JWT.
- Erste Version: 0.1.0.
- Der Betrieb ist zuerst lokal. Ein späterer Betrieb im Web ist Perspektive, siehe Deployment.

## Architektur

- Fachliche Struktur nach Domain-driven Design.
- Zwei Module: `frontend/` und `backend/`.
- Ein Prozess: das Java-Backend liefert die gebaute Oberfläche aus.
- Das Frontend spricht das Backend über REST an.
- Beträge sind `BigDecimal`.
- Datum und Zeit liegen in `Europe/Berlin`.
- Versionierung nach SemVer. Die erste gefasste Version ist 0.1.0.

## Stack

| Bereich | Festlegung |
| --- | --- |
| Backend | Quarkus, Java 25 |
| Build Backend | Maven 3.9, Wrapper `mvnw` |
| Oberfläche | React, TypeScript, PrimeReact Community |
| Frontend-Build | Vite, npm, Node 24 |
| API | REST, beschrieben mit OpenAPI |
| Persistenz | Lokal H2 als Datei. Hibernate unter Blaze Persistence, Schema mit Flyway in Standard-SQL |
| PDF | Apache PDFBox |
| Tests | JUnit 5 im Backend. Keine Frontend-Tests |

PrimeReact Community ist bei gegebener Eligibility kostenfrei. Eine Einzelperson ist zulässig. Die Version bleibt offen, ohne Festlegung auf 10 oder 11. MIT-Releases (v10) bleiben MIT. Ab v11 ist eine Community-Lizenz mit Registrierung möglich. Das Theme ist Lara Teal. In PrimeReact 10 ist das die Datei `primereact/resources/themes/lara-light-teal/theme.css`.

## Build und Start

Lokaler Start aus dem Backend-Modul:

```text
./mvnw quarkus:dev
```

Der Dev-Server hört auf Port **29215** unter dem HTTP-Root `/betriebskostenabrechnung`. Ein Befehl startet Backend und Oberfläche zusammen. Das Backend liefert die mit Vite gebaute Oberfläche aus `META-INF/resources`. Änderungen an React-Quellen erscheinen dort erst nach `npm run build` im Modul `frontend/` und einem Neuladen der Seite. Die Oberfläche ist unter `http://localhost:29215/betriebskostenabrechnung/` erreichbar.

Für Hot-Reload der Oberfläche bleibt das Backend auf Port 29215 laufen. Zusätzlich im Modul `frontend/` den Befehl `npm run dev` starten. Vite hört auf Port 5173, übersetzt die React-Quellen beim Speichern und leitet `/betriebskostenabrechnung/api` an das Backend weiter. Diese Oberfläche ist unter `http://localhost:5173/betriebskostenabrechnung/` erreichbar.

Dieselben Konsolenausgaben schreibt Quarkus zusätzlich in eine Datei im Arbeitsverzeichnis des Backends. Die aktive Logdatei heißt `logs/betriebskostenabrechnung.log` und endet auf `.log`. Ein Neustart rotiert sie nicht. Erst wenn die Datei 10 MB erreicht, bleibt `betriebskostenabrechnung.log` die aktive Datei und das Archiv heißt `betriebskostenabrechnung.log.1`.

## API

- Schnittstelle zwischen Oberfläche und Backend: REST.
- Der Vertrag der Schnittstelle liegt als OpenAPI vor.
- Die Oberfläche ist der einzige Zugang. Es gibt keine zweite Bedienung neben der Weboberfläche.

## Anmeldung

Lokal mit Anmeldung. Benutzername und Passwort werden in der Quarkus-Applikationsdatei `application.yml` gepflegt. Nach erfolgreicher Anmeldung erhält der Eigentümer ein JWT.

## Bestand in 0.1.0

- Der Start legt den Bestand aus dem Domänenmodell als Seed an: Liegenschaft Burgstraße 37, Vorderhaus und Hinterhaus, deren Mieteinheiten mit Wohnfläche und Anzahl Rauchwarnmelder, Parkplätze 1 bis 4.
- Gebäudekürzel: VH für Vorderhaus, HH für Hinterhaus.
- Die Identifikation eines Mietvertrags hat die Form `<Gebäudekürzel>-<Mieteinheit>-<Erstelldatum yyyy-MM-dd>`. Das Erstelldatum ist der Tag der Anlage in der Anwendung.
- Mieteinheiten werden nur gewählt. Sie werden nicht neu angelegt, geändert oder gelöscht.
- Die Zuordnung von Kostenart zu Kostenschlüssel am Mietvertrag folgt später und ist nicht Teil von 0.1.0.

## Persistenz

- Lokal liegen die Daten in einer H2-Datei. Es läuft kein eigener Datenbankserver.
- Hibernate arbeitet unter Blaze Persistence.
- Das Schema entsteht mit Flyway. Die lokale Datei liegt unter `backend/data/`. Liegt sie hinter den Migrationen zurück, die Dateien dort löschen und neu starten. Flyway legt den Bestand dann neu an.
- Die Migrationen sind Standard-SQL. Damit bleibt ein späterer Wechsel der Datenbank vorbereitet.
- Für den späteren Web-Betrieb ist H2 technisch möglich, aber ungeeignet: Dateisperren, Backup, mehrere Instanzen und das Hosting sprechen dagegen. Eher kommt dann PostgreSQL oder MySQL. Eine Server-Datenbank wird jetzt nicht festgelegt.

## Deployment

Heute läuft die Anwendung lokal.

Perspektive ist ein Betrieb im Web auf einem Webserver mit Java-Hosting, zum Beispiel bei Strato. Auslieferung als Fat-JAR. Dieselbe Anwendung liefert Oberfläche und Backend aus. Die Datenbank dafür ist eher PostgreSQL oder MySQL; H2 bleibt die lokale Datei. Benutzername und Passwort stehen in der Quarkus-Applikationsdatei. Die konkreten Hosting-Parameter und die Server-Datenbank sind für diesen späteren Schritt noch nicht festgelegt.

## Offen

- Konkrete Hosting-Parameter des Fat-JAR.
- Server-Datenbank für den Web-Betrieb, PostgreSQL oder MySQL, noch ohne Festlegung.
- Version von PrimeReact Community, ohne Festlegung auf 10 oder 11.
- Gepinnte Versionen von Quarkus, Blaze Persistence, Flyway, H2, PDFBox und Vite. Fest stehen Java 25, Maven 3.9 und Node 24.
