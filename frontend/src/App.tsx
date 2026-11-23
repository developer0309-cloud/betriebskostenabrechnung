import { FormEvent, useEffect, useState } from "react";
import { Button } from "primereact/button";
import { Column } from "primereact/column";
import { DataTable } from "primereact/datatable";
import { Dropdown } from "primereact/dropdown";
import { InputNumber } from "primereact/inputnumber";
import { InputText } from "primereact/inputtext";
import { Message } from "primereact/message";
import { MultiSelect } from "primereact/multiselect";
import { Password } from "primereact/password";
import { ApiFehler, api, tokenLesen, tokenSetzen } from "./api";

type Seite = "personen" | "mieter" | "vertraege" | "einheiten";

type Person = { id: number; vorname: string; nachname: string };
type Mieter = { id: number; personIds: number[]; personen: { vorname: string; nachname: string }[] };
type Einheit = {
  id: number;
  kuerzel: string;
  gebaeude: string;
  bezeichnung: string;
  wohnflaeche: number;
  rauchwarnmelder: number;
};
type Vorauszahlung = { jahr: number; betrag: number };
type VorauszahlungEntwurf = { jahr: number | null; betrag: number | null };
type Vertrag = {
  id: string;
  mieteinheitId: number;
  kuerzel: string;
  bezeichnung: string;
  mieterId: number;
  vertragsbeginn: string;
  vertragsende: string;
  vorauszahlungen: Vorauszahlung[];
};

export function App() {
  const [angemeldet, setAngemeldet] = useState(tokenLesen() != null);
  const [seite, setSeite] = useState<Seite>("personen");
  const [fehler, setFehler] = useState<string | null>(null);

  if (!angemeldet) {
    return <Login onErfolg={() => { setFehler(null); setAngemeldet(true); }} />;
  }

  return (
    <main style={{ maxWidth: 1100, margin: "0 auto", padding: 16 }}>
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1 style={{ fontSize: 24 }}>Betriebskostenabrechnung</h1>
        <Button label="Abmelden" outlined onClick={() => { tokenSetzen(null); setAngemeldet(false); }} />
      </header>
      <nav style={{ display: "flex", gap: 8, marginBottom: 16 }}>
        <Button label="Personen" onClick={() => setSeite("personen")} outlined={seite !== "personen"} />
        <Button label="Mieter" onClick={() => setSeite("mieter")} outlined={seite !== "mieter"} />
        <Button label="Mietverträge" onClick={() => setSeite("vertraege")} outlined={seite !== "vertraege"} />
        <Button label="Mieteinheiten" onClick={() => setSeite("einheiten")} outlined={seite !== "einheiten"} />
      </nav>
      {fehler && <Message severity="error" text={fehler} style={{ marginBottom: 12, width: "100%" }} />}
      {seite === "personen" && <Personen onFehler={setFehler} />}
      {seite === "mieter" && <MieterSeite onFehler={setFehler} />}
      {seite === "vertraege" && <Vertraege onFehler={setFehler} />}
      {seite === "einheiten" && <Einheiten onFehler={setFehler} />}
    </main>
  );
}

function Login({ onErfolg }: { onErfolg: () => void }) {
  const [benutzername, setBenutzername] = useState("");
  const [passwort, setPasswort] = useState("");
  const [fehler, setFehler] = useState<string | null>(null);

  async function senden(event: FormEvent) {
    event.preventDefault();
    setFehler(null);
    try {
      const antwort = await api<{ token: string }>("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ benutzername, passwort })
      });
      tokenSetzen(antwort.token);
      onErfolg();
    } catch (error) {
      tokenSetzen(null);
      setFehler(error instanceof ApiFehler ? error.message : "Anmeldung fehlgeschlagen.");
    }
  }

  return (
    <form onSubmit={senden} style={{ maxWidth: 360, margin: "80px auto", display: "grid", gap: 12 }}>
      <h1 style={{ fontSize: 24 }}>Anmeldung</h1>
      {fehler && <Message severity="error" text={fehler} />}
      <label>
        Benutzername
        <InputText value={benutzername} onChange={(e) => setBenutzername(e.target.value)} style={{ width: "100%" }} />
      </label>
      <label>
        Passwort
        <Password value={passwort} onChange={(e) => setPasswort(e.target.value)} feedback={false} toggleMask style={{ width: "100%" }} inputStyle={{ width: "100%" }} />
      </label>
      <Button type="submit" label="Anmelden" />
    </form>
  );
}

function Personen({ onFehler }: { onFehler: (meldung: string | null) => void }) {
  const [liste, setListe] = useState<Person[]>([]);
  const [vorname, setVorname] = useState("");
  const [nachname, setNachname] = useState("");
  const [bearbeitung, setBearbeitung] = useState<Person | null>(null);

  async function laden() {
    try {
      setListe(await api<Person[]>("/api/personen"));
      onFehler(null);
    } catch (error) {
      onFehler(text(error));
    }
  }

  useEffect(() => { void laden(); }, []);

  async function anlegen(event: FormEvent) {
    event.preventDefault();
    try {
      await api("/api/personen", { method: "POST", body: JSON.stringify({ vorname, nachname }) });
      setVorname("");
      setNachname("");
      await laden();
    } catch (error) {
      onFehler(text(error));
    }
  }

  async function speichern(event: FormEvent) {
    event.preventDefault();
    if (!bearbeitung) {
      return;
    }
    try {
      await api(`/api/personen/${bearbeitung.id}`, {
        method: "PUT",
        body: JSON.stringify({ vorname: bearbeitung.vorname, nachname: bearbeitung.nachname })
      });
      setBearbeitung(null);
      await laden();
    } catch (error) {
      onFehler(text(error));
    }
  }

  return (
    <section>
      <form onSubmit={anlegen} style={{ display: "flex", gap: 8, marginBottom: 12 }}>
        <InputText placeholder="Vorname" value={vorname} onChange={(e) => setVorname(e.target.value)} />
        <InputText placeholder="Nachname" value={nachname} onChange={(e) => setNachname(e.target.value)} />
        <Button type="submit" label="Anlegen" />
      </form>
      {bearbeitung && (
        <form onSubmit={speichern} style={{ display: "flex", gap: 8, marginBottom: 12 }}>
          <InputText value={bearbeitung.vorname} onChange={(e) => setBearbeitung({ ...bearbeitung, vorname: e.target.value })} aria-label="Vorname ändern" />
          <InputText value={bearbeitung.nachname} onChange={(e) => setBearbeitung({ ...bearbeitung, nachname: e.target.value })} aria-label="Nachname ändern" />
          <Button type="submit" label="Speichern" />
        </form>
      )}
      <DataTable value={liste} emptyMessage="Keine Personen">
        <Column field="vorname" header="Vorname" />
        <Column field="nachname" header="Nachname" />
        <Column header="" body={(person: Person) => (
          <span style={{ display: "flex", gap: 8 }}>
            <Button label="Ändern" outlined onClick={() => setBearbeitung(person)} />
            <Button label="Löschen" outlined severity="danger" onClick={async () => {
              try {
                await api(`/api/personen/${person.id}`, { method: "DELETE" });
                await laden();
              } catch (error) {
                onFehler(text(error));
              }
            }} />
          </span>
        )} />
      </DataTable>
    </section>
  );
}

function MieterSeite({ onFehler }: { onFehler: (meldung: string | null) => void }) {
  const [liste, setListe] = useState<Mieter[]>([]);
  const [personen, setPersonen] = useState<Person[]>([]);
  const [auswahl, setAuswahl] = useState<number[]>([]);
  const [bearbeitung, setBearbeitung] = useState<{ id: number; personIds: number[] } | null>(null);

  async function laden() {
    try {
      setListe(await api<Mieter[]>("/api/mieter"));
      setPersonen(await api<Person[]>("/api/personen"));
      onFehler(null);
    } catch (error) {
      onFehler(text(error));
    }
  }

  useEffect(() => { void laden(); }, []);

  return (
    <section>
      <form onSubmit={async (event) => {
        event.preventDefault();
        try {
          await api("/api/mieter", { method: "POST", body: JSON.stringify({ personIds: auswahl }) });
          setAuswahl([]);
          await laden();
        } catch (error) {
          onFehler(text(error));
        }
      }} style={{ display: "flex", gap: 8, marginBottom: 12 }}>
        <PersonenWahl personen={personen} value={auswahl} onChange={setAuswahl} />
        <Button type="submit" label="Mieter anlegen" />
      </form>
      {bearbeitung && (
        <form onSubmit={async (event) => {
          event.preventDefault();
          try {
            await api(`/api/mieter/${bearbeitung.id}`, {
              method: "PUT",
              body: JSON.stringify({ personIds: bearbeitung.personIds })
            });
            setBearbeitung(null);
            await laden();
          } catch (error) {
            onFehler(text(error));
          }
        }} style={{ display: "flex", gap: 8, marginBottom: 12 }}>
          <PersonenWahl personen={personen} value={bearbeitung.personIds} onChange={(personIds) => setBearbeitung({ ...bearbeitung, personIds })} />
          <Button type="submit" label="Speichern" />
        </form>
      )}
      <DataTable value={liste} emptyMessage="Keine Mieter">
        <Column field="id" header="Nr." />
        <Column header="Personen" body={(mieter: Mieter) => mieter.personen.map((p) => `${p.vorname} ${p.nachname}`).join(", ")} />
        <Column header="" body={(mieter: Mieter) => (
          <span style={{ display: "flex", gap: 8 }}>
            <Button label="Ändern" outlined onClick={() => setBearbeitung({ id: mieter.id, personIds: mieter.personIds })} />
            <Button label="Löschen" outlined severity="danger" onClick={async () => {
              try {
                await api(`/api/mieter/${mieter.id}`, { method: "DELETE" });
                await laden();
              } catch (error) {
                onFehler(text(error));
              }
            }} />
          </span>
        )} />
      </DataTable>
    </section>
  );
}

function PersonenWahl({ personen, value, onChange }: { personen: Person[]; value: number[]; onChange: (ids: number[]) => void }) {
  return (
    <MultiSelect
      value={value}
      options={personen}
      optionLabel="nachname"
      optionValue="id"
      itemTemplate={(person: Person) => `${person.vorname} ${person.nachname}`}
      onChange={(e) => onChange(e.value)}
      placeholder="Natürliche Personen"
      display="chip"
    />
  );
}

function Vertraege({ onFehler }: { onFehler: (meldung: string | null) => void }) {
  const [liste, setListe] = useState<Vertrag[]>([]);
  const [einheiten, setEinheiten] = useState<Einheit[]>([]);
  const [mieter, setMieter] = useState<Mieter[]>([]);
  const [einheitId, setEinheitId] = useState<number | null>(null);
  const [mieterId, setMieterId] = useState<number | null>(null);
  const [beginn, setBeginn] = useState("");
  const [ende, setEnde] = useState("");
  const [vorauszahlungen, setVorauszahlungen] = useState<VorauszahlungEntwurf[]>([]);
  const [bearbeitung, setBearbeitung] = useState<string | null>(null);

  async function laden() {
    try {
      setListe(await api<Vertrag[]>("/api/mietvertraege"));
      setEinheiten(await api<Einheit[]>("/api/mieteinheiten"));
      setMieter(await api<Mieter[]>("/api/mieter"));
      onFehler(null);
    } catch (error) {
      onFehler(text(error));
    }
  }

  useEffect(() => { void laden(); }, []);

  function zeileAendern(index: number, aenderung: Partial<VorauszahlungEntwurf>) {
    setVorauszahlungen(vorauszahlungen.map((eintrag, i) => i === index ? { ...eintrag, ...aenderung } : eintrag));
  }

  function mieterName(vertrag: Vertrag) {
    const eintrag = mieter.find((kandidat) => kandidat.id === vertrag.mieterId);
    if (!eintrag || eintrag.personen.length === 0) {
      return "—";
    }
    return eintrag.personen.map((person) => `${person.vorname} ${person.nachname}`).join(", ");
  }

  return (
    <section>
      <form onSubmit={async (event) => {
        event.preventDefault();
        const body = JSON.stringify({
          mieteinheitId: einheitId,
          mieterId,
          vertragsbeginn: beginn,
          vertragsende: ende,
          vorauszahlungen
        });
        try {
          if (bearbeitung) {
            await api(`/api/mietvertraege/${encodeURIComponent(bearbeitung)}`, { method: "PUT", body });
          } else {
            await api("/api/mietvertraege", { method: "POST", body });
          }
          setBearbeitung(null);
          await laden();
        } catch (error) {
          onFehler(text(error));
        }
      }} style={{ display: "grid", gridTemplateColumns: "repeat(2, minmax(0, 1fr))", gap: 8, marginBottom: 12 }}>
        <Dropdown
          value={einheitId}
          options={einheiten}
          optionValue="id"
          optionLabel="bezeichnung"
          itemTemplate={(einheit: Einheit) => `${einheit.kuerzel} ${einheit.bezeichnung}`}
          valueTemplate={(einheit: Einheit | null) => einheit ? `${einheit.kuerzel} ${einheit.bezeichnung}` : "Wohneinheit"}
          onChange={(e) => setEinheitId(e.value)}
          placeholder="Wohneinheit"
          disabled={bearbeitung != null}
        />
        <Dropdown
          value={mieterId}
          options={mieter}
          optionValue="id"
          optionLabel="id"
          itemTemplate={(eintrag: Mieter) => eintrag.personen.map((p) => `${p.vorname} ${p.nachname}`).join(", ")}
          valueTemplate={(eintrag: Mieter | null) => eintrag ? eintrag.personen.map((p) => `${p.vorname} ${p.nachname}`).join(", ") : "Mieter"}
          onChange={(e) => setMieterId(e.value)}
          placeholder="Mieter"
        />
        <label style={{ display: "grid", gap: 4 }}>
          Von
          <InputText type="date" value={beginn} onChange={(e) => setBeginn(e.target.value)} aria-label="Von" />
        </label>
        <label style={{ display: "grid", gap: 4 }}>
          Bis
          <InputText type="date" value={ende} onChange={(e) => setEnde(e.target.value)} aria-label="Bis" />
        </label>
        <div style={{ gridColumn: "1 / -1", display: "grid", gap: 8, justifyItems: "start" }}>
          {vorauszahlungen.map((eintrag, index) => (
            <span key={index} style={{ display: "flex", gap: 8 }}>
              <InputNumber
                value={eintrag.jahr}
                onValueChange={(e) => zeileAendern(index, { jahr: e.value ?? null })}
                useGrouping={false}
                min={1}
                max={9999}
                placeholder="Jahr"
                aria-label="Jahr"
              />
              <InputNumber
                value={eintrag.betrag}
                onValueChange={(e) => zeileAendern(index, { betrag: e.value ?? null })}
                mode="decimal"
                minFractionDigits={2}
                locale="de-DE"
                placeholder="Geleistete Vorauszahlung"
                aria-label="Geleistete Vorauszahlung"
              />
              <Button type="button" label="Entfernen" outlined onClick={() => setVorauszahlungen(vorauszahlungen.filter((_, i) => i !== index))} />
            </span>
          ))}
          <Button type="button" label="Jahr hinzufügen" outlined onClick={() => setVorauszahlungen([...vorauszahlungen, { jahr: null, betrag: null }])} />
          <Button type="submit" label={bearbeitung ? "Speichern" : "Vertrag anlegen"} />
        </div>
      </form>
      <DataTable value={liste} emptyMessage="Keine Mietverträge" style={volleBreite} tableStyle={{ width: "100%" }}>
        <Column field="id" header="Identifikation" />
        <Column header="Mieteinheit" body={(vertrag: Vertrag) => `${vertrag.kuerzel} ${vertrag.bezeichnung}`} />
        <Column header="Mieter" body={(vertrag: Vertrag) => mieterName(vertrag)} />
        <Column header="Von" body={(vertrag: Vertrag) => deutschesDatum(vertrag.vertragsbeginn)} />
        <Column header="Bis" body={(vertrag: Vertrag) => deutschesDatum(vertrag.vertragsende)} />
        <Column header="Vorauszahlung je Jahr" body={(vertrag: Vertrag) =>
          vertrag.vorauszahlungen.length === 0
            ? "—"
            : vertrag.vorauszahlungen.map((eintrag) => `${eintrag.jahr}: ${eintrag.betrag.toLocaleString("de-DE", { minimumFractionDigits: 2 })} €`).join(", ")
        } />
        <Column header="" body={(vertrag: Vertrag) => (
          <span style={{ display: "flex", gap: 8 }}>
          <Button label="Ändern" outlined onClick={() => {
            setBearbeitung(vertrag.id);
            setEinheitId(vertrag.mieteinheitId);
            setMieterId(vertrag.mieterId);
            setBeginn(vertrag.vertragsbeginn);
            setEnde(vertrag.vertragsende);
            setVorauszahlungen(vertrag.vorauszahlungen.map((eintrag) => ({ ...eintrag })));
          }} />
          <Button label="Löschen" outlined severity="danger" onClick={async () => {
            try {
              await api(`/api/mietvertraege/${encodeURIComponent(vertrag.id)}`, { method: "DELETE" });
              await laden();
            } catch (error) {
              onFehler(text(error));
            }
          }} />
          </span>
        )} />
      </DataTable>
    </section>
  );
}

function Einheiten({ onFehler }: { onFehler: (meldung: string | null) => void }) {
  const [liste, setListe] = useState<Einheit[]>([]);
  useEffect(() => {
    api<Einheit[]>("/api/mieteinheiten").then(setListe).catch((error) => onFehler(text(error)));
  }, []);
  return (
    <DataTable value={liste} emptyMessage="Keine Mieteinheiten" style={volleBreite} tableStyle={{ width: "100%" }}>
      <Column field="kuerzel" header="Kürzel" />
      <Column field="gebaeude" header="Gebäude" />
      <Column field="bezeichnung" header="Bezeichnung" />
      <Column field="wohnflaeche" header="Wohnfläche" />
      <Column field="rauchwarnmelder" header="Rauchwarnmelder" />
    </DataTable>
  );
}

const volleBreite = {
  width: "100vw",
  marginLeft: "calc(50% - 50vw)",
  marginRight: "calc(50% - 50vw)"
};

function deutschesDatum(iso: string) {
  const [jahr, monat, tag] = iso.split("-");
  if (!jahr || !monat || !tag) {
    return iso;
  }
  return `${tag}.${monat}.${jahr}`;
}

function text(error: unknown) {
  return error instanceof ApiFehler ? error.message : "Die Anfrage ist fehlgeschlagen.";
}
