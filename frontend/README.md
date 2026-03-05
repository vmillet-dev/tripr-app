# ⚡ Tripr App — Frontend

Modern **Angular 21** application, optimized for performance and developer experience.

---

### 🎨 Principles & Conventions

This project follows modern Angular standards to ensure maintainability and performance:

- **Standalone Components**: Modular architecture without the overhead of `NgModules`.
- **Reactive State**: Strategic use of **Signals** and **RxJS** for clean data flow.
- **Performance**: Optimization with **Zoneless** change detection.
- **Tooling**: Ultra-fast development and builds via **Vite** and **Analog**.
- **I18n**: Full internationalization support with **Transloco**.

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
