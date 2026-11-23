import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  base: "/betriebskostenabrechnung/",
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      "/betriebskostenabrechnung/api": {
        target: "http://localhost:29215",
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: "../backend/src/main/resources/META-INF/resources",
    emptyOutDir: true
  }
});
