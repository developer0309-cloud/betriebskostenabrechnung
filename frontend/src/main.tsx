import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { localeOptions, updateLocaleOptions } from "primereact/api";
import { App } from "./App";
import "primereact/resources/themes/lara-light-teal/theme.css";
import "primereact/resources/primereact.min.css";
import "primeicons/primeicons.css";

const bisher = localeOptions("en") as { aria?: Record<string, string> };
updateLocaleOptions({
  aria: {
    ...bisher.aria,
    passwordShow: "Passwort anzeigen",
    passwordHide: "Passwort verbergen"
  }
}, "en");

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
