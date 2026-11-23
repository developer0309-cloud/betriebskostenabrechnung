const TOKEN = "betriebskosten-token";
const BASIS = "/betriebskostenabrechnung";

export function tokenLesen(): string | null {
  return sessionStorage.getItem(TOKEN);
}

export function tokenSetzen(token: string | null) {
  if (token) {
    sessionStorage.setItem(TOKEN, token);
  } else {
    sessionStorage.removeItem(TOKEN);
  }
}

export class ApiFehler extends Error {
  constructor(message: string) {
    super(message);
  }
}

export async function api<T>(pfad: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  if (init.body) {
    headers.set("Content-Type", "application/json");
  }
  const token = tokenLesen();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  const ziel = pfad.startsWith("/") ? `${BASIS}${pfad}` : `${BASIS}/${pfad}`;
  const antwort = await fetch(ziel, { ...init, headers });
  if (antwort.status === 401) {
    tokenSetzen(null);
    throw new ApiFehler("Anmeldung erforderlich.");
  }
  if (!antwort.ok) {
    const text = await antwort.text();
    let meldung = antwort.statusText;
    try {
      meldung = JSON.parse(text).meldung ?? meldung;
    } catch {
      if (text) {
        meldung = text;
      }
    }
    throw new ApiFehler(meldung);
  }
  if (antwort.status === 204) {
    return undefined as T;
  }
  return antwort.json() as Promise<T>;
}
