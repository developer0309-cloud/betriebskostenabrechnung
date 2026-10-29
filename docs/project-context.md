# Projekt

Jährliche Betriebskostenabrechnung für die eigenen Mieter erstellen.

## Rahmen

### Zweck

- Einzelnutzer: nur der Eigentümer
- Läuft nur lokal, ohne Veröffentlichung
- Sprache der Anwendung: Deutsch
- Bedienung nur über die Weboberfläche
- Ausgabe der Abrechnungen als PDF mit Apache PDFBox
- Kein Login: die lokale Oberfläche ist ohne Anmeldung erreichbar

### Fachlichkeit

- Architektur: Domain-driven Design
- Beträge als BigDecimal, Datum und Zeit in Europe/Berlin

### Aufbau

- Ein Prozess: das Java-Backend liefert die gebaute Oberfläche aus
- Start: ein lokaler Befehl startet Frontend und Backend zusammen
- Schnittstelle: REST vom React-Frontend zum Java-Backend
- Daten: persistent in H2 als lokale Datei, ohne Datenbankserver und ohne Lizenzkosten
- Persistenz: Hibernate unter Blaze Persistence, Schema mit Flyway in Standard-SQL
- Oberfläche: Web-GUI mit React, TypeScript und PrimeReact
- Backend: Java mit Blaze Persistence und Standard-SQL, kein Python

### Werkzeuge

- Build: Maven 3.9, Java 25
- Frontend-Build: Vite und npm, Node 24
- Tests: JUnit 5
- Versionierung: semantische Versionierung

### Ablage

- Repository: `C:\github\betriebskostenabrechnung` auf DESKTOP-I3UF79N
- GitHub: https://github.com/developer0309-cloud/betriebskostenabrechnung
- Arbeitsbranch: `init` (noch ohne Commit)

## Offen

- Gebäude, Wohnungen und Mieter
- Abrechnungszeitraum und Umlageschlüssel
- Kostenarten und Belege
