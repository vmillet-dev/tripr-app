# Tripr App Frontend

Angular 19 frontend for the Tripr travel planning application.

## Architecture

The frontend is built with Angular 19 using a feature-based architecture:

```
src/
├── app/
│   ├── core/              # Core functionality (services, guards, interceptors)
│   │   ├── components/    # Shared components
│   │   ├── guards/        # Route guards
│   │   ├── interceptors/  # HTTP interceptors
│   │   ├── models/        # Data models
│   │   └── services/      # Core services
│   ├── features/          # Feature modules
│   │   ├── auth/          # Authentication features
│   │   ├── dashboard/     # Dashboard features
│   │   └── home/          # Home page features
│   └── transloco/         # Internationalization
└── environments/          # Environment configurations
```

### Key Features

- **Authentication**: Login, registration, and password reset workflows
- **Internationalization**: Multi-language support with Transloco
- **Responsive Design**: Mobile-friendly UI components
- **HTTP Interceptors**: JWT token handling and error handling
- **Route Guards**: Protected routes for authenticated users

## Development

### Prerequisites

- Node.js 18+
- npm 9+

### Installation

```bash
npm install
```

### Running the Development Server

```bash
ng serve
```

The application will be available at http://localhost:4200 and will automatically reload when you make changes to the source files.

### Building for Production

```bash
ng build
```

The build artifacts will be stored in the `dist/` directory.

### Running Unit Tests

```bash
ng test
```

### Code Scaffolding

Generate new components, directives, pipes, services, etc:

```bash
ng generate component components/component-name
ng generate service services/service-name
```

## Best Practices

- Use standalone components where possible
- Follow Angular style guide for naming conventions
- Implement lazy loading for feature modules
- Use reactive forms for complex form handling
- Add comprehensive unit tests for services and components
