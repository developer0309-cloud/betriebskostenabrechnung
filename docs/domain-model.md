# Domänenmodell

Jährliche Verteilung der Betriebskosten eines Einzeleigentümers für seine Mieter. Der Eigentümer arbeitet allein und lokal. Die Verteilung betrifft die Mieteinheiten und wird als PDF ausgegeben. Ein Parkplatz bleibt außen vor.

Konkrete Beteiligungen, natürliche Personen und Beträge sind noch nicht genannt. Sie stehen im Modell als Platzhalter. Die Bezeichnung der Liegenschaft ist Burgstraße 37. Sie besteht aus den Gebäuden Vorderhaus und Hinterhaus und aus vier Parkplätzen mit den Nummern 1, 2, 3 und 4. Das Vorderhaus besteht aus den Mieteinheiten EG, 1. OG, 2. OG, 3. OG und DG. Deren Wohnfläche in Quadratmetern ist EG 45,75, 1. OG 84,15, 2. OG 84,15, 3. OG 84,15 und DG 81,25. Die Anzahl Rauchwarnmelder im Vorderhaus ist EG 2, 1. OG 3, 2. OG 2, 3. OG 3 und DG 3. Das Hinterhaus besteht aus den Mieteinheiten Lager, 1. OG, 2. OG und DG. Deren Wohnfläche in Quadratmetern ist Lager 46,12, 1. OG 44,39, 2. OG 44,39 und DG 39,95. Die Anzahl Rauchwarnmelder im Hinterhaus ist Lager 0, 1. OG 3, 2. OG 3 und DG 2.

## Bounded Context

**Name:** Betriebskosten

Ein Kontext. Bestand und die jährliche Verteilung der Betriebskosten gehören zur selben Fachsprache und zum selben jährlichen Vorgang des Eigentümers. Getrennt sind nur die Aggregates, weil Liegenschaft, natürliche Person, Mieter, Mietvertrag und Verteilung unterschiedlich lange leben.

| Begriff | Bedeutung in diesem Kontext |
| --- | --- |
| Liegenschaft | Wirtschaftliche Einheit des Eigentümers. Die Bezeichnung ist Burgstraße 37. Besteht aus den Gebäuden Vorderhaus und Hinterhaus und aus vier Parkplätzen |
| Gebäude | Bauwerk der Liegenschaft. Die Mieteinheiten hängen am Gebäude. Kürzel VH für Vorderhaus, HH für Hinterhaus. Vorderhaus: EG 45,75, 1. OG 84,15, 2. OG 84,15, 3. OG 84,15, DG 81,25 Quadratmeter Wohnfläche. Hinterhaus: Lager 46,12, 1. OG 44,39, 2. OG 44,39, DG 39,95 Quadratmeter Wohnfläche |
| Parkplatz | Stellplatz der Liegenschaft. Die vier Parkplätze haben die Nummern 1, 2, 3 und 4. Bleibt bei der Verteilung der Betriebskosten außen vor |
| Mieteinheit | Einheit am Gebäude. Hat Bezeichnung, Grundfläche, Anzahl Rauchwarnmelder und Kostenbeteiligung. Anzahl Rauchwarnmelder im Vorderhaus: EG 2, 1. OG 3, 2. OG 2, 3. OG 3, DG 3. Im Hinterhaus: Lager 0, 1. OG 3, 2. OG 3, DG 2. Die Verteilung der Betriebskosten betrifft sie. Die Bezeichnung ist innerhalb des Gebäudes eindeutig. „Lager“ ist nur die Bezeichnung einer Mieteinheit im Hinterhaus |
| Natürliche Person | Mensch mit Vorname und Nachname |
| Mieter | Eine oder mehrere natürliche Personen, ohne eigenen Namen |
| Mietvertrag | Regelt die Beziehung genau einer Mieteinheit und genau eines Mieters. Die Identifikation lautet `<Gebäudekürzel>-<Mieteinheit>-<Erstelldatum yyyy-MM-dd>`. Hat tagesbasierten Vertragsbeginn und tagesbasiertes Vertragsende. Erfasst je Kalenderjahr die geleistete Betriebskostenvorauszahlung in EUR. Enthält die Zuordnung von Kostenart zu Kostenschlüssel |
| Verteilung | Verteilung der Betriebskosten genau einer Liegenschaft. Das Ende liegt zwölf Monate nach dem Beginn. Darin liegen Kostenarten und Belege. Eine festgestellte Verteilung bleibt unverändert |
| Kostenart | Umlagefähige Kostengruppe nach der Betriebskostenverordnung (BetrKV). Zu ihr gehört mindestens ein Beleg. Bekannt sind Grundsteuer, Wartung Rauchwarnmelder, Allgemeinstrom, Hausreinigung, Gebäudeversicherung, Glasversicherung, Strassenreinigung, Entwässerung und Müllentsorgung |
| Beleg | Nachweis eines Kostenbetrags innerhalb einer Kostenart |
| Kostenschlüssel | Besteht aus einem eindeutigen Schlüsselwert und einer Beschreibung. Bekannt sind Wohnfläche mit „Umlage nach Wohnfläche in Quadratmeter“, direkt mit „Direkte Kostenzuordnung“ und RWM mit „Anzahl Rauchwarnmelder“ |

Der Kontext erfasst die umlagefähigen Betriebskosten und ihre Verteilung. Der Volltext des Mietvertrags und der Zahlungsverkehr bleiben außerhalb. Hier zählen Identifikation, Vertragszeit, die je Kalenderjahr geleistete Betriebskostenvorauszahlung, die Zuordnung von Kostenart zu Kostenschlüssel, Kostenanteile und der Saldo.

## Aggregates

Fünf Aggregates. Änderungen innerhalb eines Aggregates bleiben in sich stimmig. Zwischen Aggregates gelten Verweise auf die Identität.

| Aggregate-Wurzel | Enthaltene Entitäten | Lebensdauer |
| --- | --- | --- |
| Liegenschaft | Gebäude, Mieteinheit, Parkplatz | Länger als die Mietverträge |
| Natürliche Person | — | Unabhängig von den Verträgen |
| Mieter | — | Über mehrere Mietverträge |
| Mietvertrag | — | Vom Vertragsbeginn bis zum Vertragsende |
| Verteilung | Kostenart, Beleg | Zwölf Monate, danach unverändert |

### Liegenschaft

Wurzel des Bestands.

**Entitäten**

- **Liegenschaft.** Identität und Bezeichnung. Die Bezeichnung ist Burgstraße 37. Besteht aus genau zwei Gebäuden, genannt Vorderhaus und Hinterhaus, und aus genau vier Parkplätzen.
- **Gebäude.** Gehört zu genau einer Liegenschaft. Die Namen der beiden Gebäude sind Vorderhaus und Hinterhaus. Das Kürzel von Vorderhaus ist VH, das Kürzel von Hinterhaus ist HH. Die Mieteinheiten hängen am Gebäude. Das Vorderhaus besteht aus den Mieteinheiten EG, 1. OG, 2. OG, 3. OG und DG. Deren Wohnfläche in Quadratmetern ist EG 45,75, 1. OG 84,15, 2. OG 84,15, 3. OG 84,15 und DG 81,25. Das Hinterhaus besteht aus den Mieteinheiten Lager, 1. OG, 2. OG und DG. Deren Wohnfläche in Quadratmetern ist Lager 46,12, 1. OG 44,39, 2. OG 44,39 und DG 39,95. „Lager“ ist hier nur die Bezeichnung, kein eigener Begriff.
- **Mieteinheit.** Gehört zu genau einem Gebäude. Hat Bezeichnung, Grundfläche, Anzahl Rauchwarnmelder und die Beteiligung an Reinigungsservice, Hausmeister und Heizungswartung. Die Grundfläche der Mieteinheiten ist ihre Wohnfläche. Die Anzahl Rauchwarnmelder im Vorderhaus ist EG 2, 1. OG 3, 2. OG 2, 3. OG 3 und DG 3. Im Hinterhaus ist sie Lager 0, 1. OG 3, 2. OG 3 und DG 2. Die Beteiligung ist ein Platzhalter. Die Verteilung der Betriebskosten betrifft sie. Die Bezeichnung ist innerhalb des Gebäudes eindeutig.
- **Parkplatz.** Gehört zu genau einer Liegenschaft. Die vier Parkplätze haben die Nummern 1, 2, 3 und 4. Ein Parkplatz bleibt bei der Verteilung der Betriebskosten außen vor. Ein Parkplatz ist keine Mieteinheit.

**Wertobjekte**

- **Bezeichnung.** Bezeichnung der Liegenschaft. Sie lautet Burgstraße 37.
- **Name.** Name eines Gebäudes. Die Namen sind Vorderhaus und Hinterhaus.
- **Kürzel.** Kurzzeichen eines Gebäudes. VH steht für Vorderhaus, HH für Hinterhaus.
- **Nummer.** Nummer eines Parkplatzes. Die Nummern sind 1, 2, 3 und 4.
- **Grundfläche.** Quadratmeter einer Mieteinheit, größer als null. Das ist die Wohnfläche. Vorderhaus: EG 45,75, 1. OG 84,15, 2. OG 84,15, 3. OG 84,15, DG 81,25. Hinterhaus: Lager 46,12, 1. OG 44,39, 2. OG 44,39, DG 39,95.
- **Anzahl Rauchwarnmelder.** Ganze Zahl größer oder gleich null, an der Mieteinheit. Vorderhaus: EG 2, 1. OG 3, 2. OG 2, 3. OG 3, DG 3. Hinterhaus: Lager 0, 1. OG 3, 2. OG 3, DG 2.
- **Kostenbeteiligung.** Je Reinigungsservice, Hausmeister und Heizungswartung ein ja oder nein, an der Mieteinheit.

**Invarianten**

- Die Bezeichnung der Liegenschaft ist Burgstraße 37.
- Eine Liegenschaft besteht aus genau zwei Gebäuden und genau vier Parkplätzen.
- Die beiden Gebäude heißen Vorderhaus und Hinterhaus. Die Namen sind disjunkt. Die Kürzel sind VH und HH und ebenfalls disjunkt.
- Das Vorderhaus besteht aus genau fünf Mieteinheiten mit den Bezeichnungen EG, 1. OG, 2. OG, 3. OG und DG. Ihre Wohnfläche in Quadratmetern ist EG 45,75, 1. OG 84,15, 2. OG 84,15, 3. OG 84,15 und DG 81,25. Die Anzahl Rauchwarnmelder ist EG 2, 1. OG 3, 2. OG 2, 3. OG 3 und DG 3.
- Das Hinterhaus besteht aus genau vier Mieteinheiten mit den Bezeichnungen Lager, 1. OG, 2. OG und DG. Ihre Wohnfläche in Quadratmetern ist Lager 46,12, 1. OG 44,39, 2. OG 44,39 und DG 39,95. Die Anzahl Rauchwarnmelder ist Lager 0, 1. OG 3, 2. OG 3 und DG 2.
- Die Bezeichnung einer Mieteinheit ist innerhalb ihres Gebäudes eindeutig.
- Jede Mieteinheit gehört zu genau einem dieser Gebäude.
- Jede Mieteinheit hat eine Grundfläche größer als null und eine Anzahl Rauchwarnmelder größer oder gleich null.
- Jede Mieteinheit beteiligt sich an Reinigungsservice, Hausmeister und Heizungswartung jeweils mit ja oder nein.
- Die Verteilung der Betriebskosten betrifft jede Mieteinheit.
- Jeder Parkplatz gehört zur Liegenschaft. Die vier Parkplätze haben die Nummern 1, 2, 3 und 4. Ein Parkplatz ist keine Mieteinheit und bleibt bei der Verteilung der Betriebskosten außen vor.

### Natürliche Person

Wurzel für den Menschen. Eine natürliche Person hat einen Vornamen und einen Nachnamen.

**Entitäten**

- **Natürliche Person.** Identität, Vorname und Nachname. Vorname und Nachname sind Platzhalter.

**Wertobjekte**

- **Vorname.** Rufname der natürlichen Person.
- **Nachname.** Familienname der natürlichen Person.

**Invarianten**

- Jede natürliche Person hat einen Vornamen und einen Nachnamen.

### Mieter

Wurzel für die Zuordnung der Menschen. Ein Mieter ist eine natürliche Person oder mehrere natürliche Personen und trägt selbst keinen Namen.

**Entitäten**

- **Mieter.** Identität und die zugehörigen natürlichen Personen.

**Invarianten**

- Ein Mieter umfasst mindestens eine natürliche Person. Mehrere natürliche Personen an demselben Mieter sind zulässig.
- Der Mieter trägt keinen Vornamen und keinen Nachnamen.
- Die Identität des Mieters bleibt über mehrere Mietverträge erhalten.

### Mietvertrag

Wurzel für die Beziehung von Mieteinheit und Mieter.

**Entitäten**

- **Mietvertrag.** Regelt die Beziehung genau einer Mieteinheit und genau eines Mieters. Hat eine eindeutige Identifikation, einen tagesbasierten Vertragsbeginn und ein tagesbasiertes Vertragsende. Enthält je Kalenderjahr die geleistete Betriebskostenvorauszahlung und die Zuordnung von Kostenart zu Kostenschlüssel.

**Wertobjekte**

- **Identifikation.** Kennung des Mietvertrags, eindeutig unter allen Mietverträgen. Sie hat die Form `<Gebäudekürzel>-<Mieteinheit>-<Erstelldatum yyyy-MM-dd>`. Gebäudekürzel ist VH oder HH. Mieteinheit ist die Bezeichnung der gewählten Mieteinheit. Das Erstelldatum ist der Tag der Anlage, in Europe/Berlin.
- **Tag.** Ein Kalendertag. Vertragsbeginn und Vertragsende sind Tage.
- **Vertragszeit.** Die Tage vom Vertragsbeginn bis zum Vertragsende, beide Tage eingeschlossen.
- **Geleistete Betriebskostenvorauszahlung.** Der Betrag in EUR, den der Mieter in einem Kalenderjahr für diesen Mietvertrag geleistet hat. Sie besteht aus dem Jahr und einem Geldbetrag. Je Kalenderjahr höchstens ein Betrag. Die Höhe ist ein Platzhalter.
- **Geldbetrag.** Euro, auf den Cent genau, größer oder gleich null.
- **Zuordnung.** Verbindet in diesem Mietvertrag genau eine Kostenart mit genau einem Kostenschlüssel. Die konkreten Paare sind Platzhalter.

**Invarianten**

- Ein Mietvertrag hat genau eine eindeutige Identifikation, genau eine Mieteinheit und genau einen Mieter. Die Identifikation folgt `<Gebäudekürzel>-<Mieteinheit>-<Erstelldatum yyyy-MM-dd>`.
- Vertragsbeginn und Vertragsende sind Kalendertage. Der Vertragsbeginn liegt nicht nach dem Vertragsende.
- Je Kalenderjahr, das die Vertragszeit schneidet, gibt es höchstens eine geleistete Betriebskostenvorauszahlung. Ein Jahr außerhalb der Vertragszeit hat keinen Eintrag.
- Alle natürlichen Personen dieses Mieters sind für die gesamte Vertragszeit zugeordnet.
- Eine Mieteinheit kann mehrere Mietverträge haben. Deren Vertragszeiten sind getrennt und haben keinen gemeinsamen Tag.
- Der Mietvertrag enthält die Zuordnung von Kostenart zu Kostenschlüssel. Jede Kostenart kommt darin höchstens einmal vor.

### Verteilung

Wurzel des Vorgangs einer Liegenschaft. Eine festgestellte Verteilung bleibt unverändert. Eine Korrektur ist eine neue Fassung.

**Entitäten**

- **Verteilung.** Verweist auf genau eine Liegenschaft. Das Ende liegt zwölf Monate nach dem Beginn. Status ist `Entwurf` oder `festgestellt`. Hat eine Fassung.
- **Kostenart.** Kostengruppe in dieser Verteilung, mit mindestens einem Beleg. Welcher Kostenschlüssel sie verteilt, steht in der Zuordnung des Mietvertrags. Die bekannten Kostenarten sind Grundsteuer, Wartung Rauchwarnmelder, Allgemeinstrom, Hausreinigung, Gebäudeversicherung, Glasversicherung, Strassenreinigung, Entwässerung und Müllentsorgung.
- **Beleg.** Nachweis innerhalb genau einer Kostenart. Aussteller, Belegdatum, Leistungszeitraum und Beträge sind Platzhalter.

**Wertobjekte**

- **Beteiligungszeitraum.** Schnittmenge aus der Vertragszeit des Mietvertrags und der Verteilung.
- **Kostenschlüssel.** Besteht aus einem Schlüsselwert und einer Beschreibung.
- **Schlüsselwert.** Kennung eines Kostenschlüssels, eindeutig unter allen Kostenschlüsseln. Bekannt sind Wohnfläche, direkt und RWM.
- **Beschreibung.** Text zum Schlüsselwert. Zur Wohnfläche lautet sie „Umlage nach Wohnfläche in Quadratmeter“, zu direkt „Direkte Kostenzuordnung“ und zu RWM „Anzahl Rauchwarnmelder“.
- **Kostenanteil.** Geldbetrag des Mietvertrags an einer Kostenart.
- **Saldo.** Kostenanteil minus angerechnete Betriebskostenvorauszahlungen. Positiv ist eine Nachzahlung des Mieters, negativ ein Guthaben des Mieters. Gilt für den einen Mieter des Mietvertrags, gemeinsam für seine natürlichen Personen.
- **Eigentümeranteil.** Anteil aus Leerstand und aus nicht umlagefähigen Belegteilen. Er bleibt beim Eigentümer.

**Invarianten**

- Das Ende der Verteilung liegt zwölf Monate nach dem Beginn. Verteilungen derselben Liegenschaft überlappen sich nicht.
- Jeder Kostenschlüssel hat genau einen Schlüsselwert und genau eine Beschreibung. Die Schlüsselwerte sind disjunkt.
- Es gibt den Kostenschlüssel mit dem Schlüsselwert Wohnfläche und der Beschreibung „Umlage nach Wohnfläche in Quadratmeter“.
- Es gibt den Kostenschlüssel mit dem Schlüsselwert direkt und der Beschreibung „Direkte Kostenzuordnung“.
- Es gibt den Kostenschlüssel mit dem Schlüsselwert RWM und der Beschreibung „Anzahl Rauchwarnmelder“.
- Zu jeder Kostenart gehört mindestens ein Beleg.
- Ein Beleg gehört zu genau einer Kostenart. Wirksam ist der Teil seines Leistungszeitraums, der in der Verteilung liegt.
- Für jeden Mietvertrag, dessen Vertragszeit die Verteilung schneidet, gibt es genau einen Beteiligungszeitraum, Kostenanteile und genau einen Saldo.
- Ein Parkplatz hat keinen Beteiligungszeitraum und keinen Saldo.
- Saldo gleich Kostenanteil minus angerechnete Betriebskostenvorauszahlungen.
- Je Kostenart gilt: Summe der Kostenanteile plus Eigentümeranteil aus Leerstand gleich dem umlagefähigen Betrag.
- Restcents bei der Verteilung werden nach unten abgerundet.
- Nach der Feststellung gehen Kostenanteile, angerechnete Betriebskostenvorauszahlungen und der Saldo eines Mietvertrags als PDF dem Mieter innerhalb von zwölf Monaten nach Ende der Verteilung zu.

## Kostenarten

Bekannte Kostenarten:

- Grundsteuer
- Wartung Rauchwarnmelder
- Allgemeinstrom
- Hausreinigung
- Gebäudeversicherung
- Glasversicherung
- Strassenreinigung
- Entwässerung
- Müllentsorgung

Die Kostenbeteiligung an Reinigungsservice, Hausmeister und Heizungswartung sagt, ob die Mieteinheit an diesen Kosten teilnimmt. Welchen Kostenschlüssel der Mietvertrag einer Kostenart zuordnet, ist noch offen.

## Platzhalter

Noch nicht genannt und deshalb ohne konkreten Inhalt:

- Kostenbeteiligung der Mieteinheiten
- Vorname und Nachname der natürlichen Personen
- Vertragsbeginn, Vertragsende, die je Jahr geleistete Betriebskostenvorauszahlung und die Zuordnung von Kostenart zu Kostenschlüssel der Mietverträge
- Konkrete Beginn- und Enddaten der Verteilungen
- Aussteller, Daten und Beträge der Belege
- Erfasste Beträge der geleisteten Betriebskostenvorauszahlung

## Offen

- Welche Kostenart ein Mietvertrag welchem Kostenschlüssel zuordnet
- Zählweise der Personen und Erfassung des Verbrauchs
- Konkrete Umlageklauseln der Mietverträge
