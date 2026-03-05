# ⚡ Tripr App — Frontend

Modern **Angular 21** application, optimized for performance and developer experience.

---

### 🎨 Principles & Conventions

This project embraces the latest innovations in the Angular ecosystem:

- **Signals**: Reactive and granular state management.
- **Zoneless**: Removal of `zone.js` for increased performance and better control over the rendering cycle.
- **Standalone Components**: Modular architecture without `NgModules`.
- **Vite & Analog**: Ultra-fast tooling for development and build.
- **Internationalization**: Multi-language support via **Transloco**.

---

### 📁 Folder Structure (`src/app/`)

A clean organization by responsibility:

- **`core/`**: Global services (Auth, Interceptors, Guards, Generated API).
- **`features/`**: Business modules loaded via *lazy-loading* (Home, Auth, Dashboard).
- **`shared/`**: Reusable UI components, Pipes, and Directives.

---

### 🚀 Useful Commands

| Action               | Command                |
|:---------------------|:-----------------------|
| **Installation**     | `npm install`          |
| **Development**      | `npm run dev`          |
| **Production Build** | `npm run build`        |
| **Generate API**     | `npm run api:generate` |

### 🧪 Tests & Quality

We use **Vitest** for ultra-fast unit and component testing.

```bash
npm test        # Run all tests
npm run test:ui # Vitest visual interface
```

---

### 🛠️ Troubleshooting

- **API Synchronization**: If TypeScript models don't match the backend, run `npm run api:generate`.
- **Node Version**: Ensure you are using **Node 24+**.
- **CORS**: CORS issues are generally avoided thanks to the Vite proxy. Check `vite.config.ts` if problems persist.
