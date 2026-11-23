# Release Notes

Versionierung nach SemVer.

## 0.2.0

- CRUD für Belege im Bereich Kosten: Valutadatum, Betrag, optionale Beschreibung, Kostenart und Kennzeichen direkt oder indirekt. Direkte Belege haben fest die Kostenart Direkte Kosten und einen Mieter. Indirekte Belege haben eine der übrigen Kostenarten. Die Identifikation entsteht bei der Anlage.

## 0.1.0

Erste lokale Fassung.

- Anmeldung mit Benutzername und Passwort aus `application.yml`. Die Sitzung läuft über JWT.
- Seed der Liegenschaft Burgstraße 37: Vorderhaus (VH), Hinterhaus (HH), Mieteinheiten mit Wohnfläche und Rauchwarnmeldern, Parkplätze 1 bis 4.
- CRUD für natürliche Personen, Mieter und Mietverträge. Am Mietvertrag wird je Kalenderjahr die geleistete Betriebskostenvorauszahlung erfasst. Mieteinheiten nur als Liste und Auswahl.
- Java-Package `de.hv.burgstrasse.betriebskostenabrechnung`.
- Oberfläche mit PrimeReact Lara Teal.
- Konfiguration in `application.yml`. HTTP-Root ist `/betriebskostenabrechnung`.
- Konsolenausgaben zusätzlich in die aktive Logdatei `logs/betriebskostenabrechnung.log`.
