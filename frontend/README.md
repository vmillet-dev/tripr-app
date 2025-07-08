# Frontend

[![Build Status](https://github.com/vmillet-dev/tripr-app/workflows/Build%20and%20Test/badge.svg)](https://github.com/vmillet-dev/tripr-app/actions)
[![Angular](https://img.shields.io/badge/Angular-19.2.0-red.svg)](https://angular.io/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7.2-blue.svg)](https://www.typescriptlang.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.5-purple.svg)](https://getbootstrap.com/)

Angular 19 frontend application with JWT token management, internationalization, and responsive Bootstrap UI.

## Table of Contents

- [Technical Details](#technical-details)
- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)

## Technical Details

### Framework
- **Angular 19.2.0** - Modern web framework with standalone components
- **TypeScript 5.7.2** - Type-safe JavaScript development with strict mode
- **Node.js 22+** - JavaScript runtime for development and build tools
- **npm/pnpm** - Package management and dependency resolution

### Authentication
**JWT and Refresh Token Handling Implementation:**
- **Access Token Storage** - Memory-based storage for security
- **Refresh Token Management** - HTTP-only cookies for automatic renewal
- **HTTP Interceptors** - Automatic token attachment and refresh handling
- **Route Guards** - Protected routes with authentication checks
- **Token Expiration Handling** - Seamless token refresh without user interruption

### State Management
**Service-Based State Management:**
- **Angular Services** - Singleton services for global state
- **RxJS Observables** - Reactive state management with BehaviorSubject

## Development Setup

### Prerequisites

Ensure you have the following tools installed with the specified minimum versions:

| Tool            | Version | Purpose            | Verification     |
|-----------------|---------|--------------------|------------------|
| **Node.js**     | 22+     | JavaScript runtime | `node --version` |
| **npm**         | 10+     | Package manager    | `npm --version`  |
| **Angular CLI** | 19+     | Development tools  | `ng version`     |

### Installation

#### 1. Install Dependencies

```bash
cd frontend

# Install all dependencies
npm install
```


#### 2. Install Angular CLI (if not installed globally)

```bash
# Install Angular CLI globally
npm install -g @angular/cli@19

# Verify installation
ng version
```

### Running the Application

#### Development Server

```bash
cd frontend

# Start development server
npm start
# or
ng serve

# Start with specific port
ng serve --port 4200

# Start with host binding (for network access)
ng serve --host 0.0.0.0
```

**Expected output:**
```
✅ Local:   http://localhost:4200/

✅ Application bundle generation complete.
✅ watch mode enabled. watching for file changes...
```

#### Production Preview

```bash
# Build for production
npm run build

# Serve production build locally
npm run serve:prod
```

### Build Process

#### Development Build

```bash
# Watch mode for continuous building
ng build --watch
```

#### Production Build

```bash
# Optimized production build
npm run build
# or
ng build --configuration production

# Build with bundle analysis
ng build --stats-json
npx webpack-bundle-analyzer dist/tripr-frontend/stats.json
```

**Build Output:**
```
dist/tripr-frontend/
├── index.html              # Main HTML file
├── main.[hash].js          # Application bundle
├── vendor.[hash].js        # Third-party dependencies
├── runtime.[hash].js       # Angular runtime
├── styles.[hash].css       # Compiled styles
└── assets/                 # Static assets
```

## Project Structure

### Component Architecture

The frontend follows Angular's recommended architecture with standalone components:

```
src/
├── app/
│   ├── core/                           # Core singleton services and guards
│   │   ├── components/                 # Shared UI components
│   │   │   ├── header/                 # Navigation header
│   │   ├── guards/                     # Route protection
│   │   │   ├── auth.guard.ts           # Authentication guard
│   │   ├── interceptors/               # HTTP request/response handling
│   │   │   ├── auth.interceptor.ts     # JWT token attachment
│   │   ├── models/                     # TypeScript interfaces and types
│   │   │   ├── auth.model.ts           # Authentication models
│   │   │   └── password-reset.model.ts # API response models
│   │   └── services/                   # Core business services
│   │       ├── auth.service.ts         # Authentication logic
│   │       └── storage.service.ts      # Local/session storage
│   ├── features/                       # Feature-based modules
│   │   ├── auth/                       # Authentication features
│   │   │   ├── components/             # Auth-specific components
│   │   │   │   ├── login/              # Login form component
│   │   │   │   ├── register/           # Registration form
│   │   │   │   ├── forgot-password/    # Password reset request
│   │   │   │   └── reset-password/     # Password reset form
│   │   ├── dashboard/                  # User dashboard
│   │   └── home/                       # Public home page
│   ├── transloco/                      # Internationalization
│   │   ├── transloco-root.module.ts    # Transloco configuration
│   │   └── transloco.config.ts         # Translation settings
│   ├── app.component.ts                # Root application component
│   ├── app.config.ts                   # Application configuration
│   └── app.routes.ts                   # Main routing configuration
├── assets/                             # Static assets
│   ├── i18n/                           # Translation files
│   │   ├── en.json                     # English translations
│   │   └── fr.json                     # French translations
├── environments/                       # Environment configurations
│   ├── environment.ts                  # Development environment
│   ├── environment.prod.ts             # Production environment
├── styles/                             # Global styles
│   ├── _variables.scss                 # SCSS variables
│   ├── _mixins.scss                    # SCSS mixins
│   └── styles.scss                     # Main stylesheet
├── index.html                          # Main HTML template
├── main.ts                             # Application bootstrap
└── polyfills.ts                        # Browser compatibility
```

## Troubleshooting

### Common Setup Issues

#### Node.js/npm Issues

**Problem**: Dependencies fail to install

**Solution**:
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall dependencies
npm install

# Use specific Node.js version
nvm use 22
npm install
```

#### Angular CLI Issues

**Problem**: Angular CLI commands fail

**Solution**:
```bash
# Install Angular CLI globally
npm install -g @angular/cli@19

# Clear Angular CLI cache
ng cache clean

# Verify installation
ng version
```

#### Build Errors

**Problem**: TypeScript compilation errors

**Solution**:
```bash
# Check TypeScript configuration
npx tsc --noEmit

# Update TypeScript
npm update typescript

# Clear Angular build cache
rm -rf .angular/cache
ng build
```

#### Development Server Issues

**Problem**: Development server fails to start

**Solution**:
```bash
# Check if port is in use
lsof -i :4200

# Use different port
ng serve --port 4201

# Clear browser cache and restart
ng serve --disable-host-check
```

### Performance Issues

#### Slow Build Times

**Problem**: Build takes too long

**Solution**:
```bash
# Increase Node.js memory limit
export NODE_OPTIONS="--max-old-space-size=8192"

# Use development build
ng build
```

**Built with ❤️ using Angular and TypeScript. Happy coding! 🚀**
