import {ApplicationConfig, inject, provideAppInitializer, provideZonelessChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {routes} from './app.routes';
import {translocoProviders} from './transloco/transloco-root.module';
import {AuthService} from './core/services/auth.service';
import {authInterceptor} from './core/interceptors/auth.interceptor';
import {firstValueFrom} from "rxjs";
import {provideApi} from "./core/api/generated";

export const appConfig: ApplicationConfig = {
    providers: [
        ...translocoProviders,
        provideZonelessChangeDetection(),
        provideRouter(routes),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideAppInitializer(async () => {
            const auth = inject(AuthService);
            return firstValueFrom(auth.refreshToken()).catch(() => false);
        }),
        provideApi("/api")
    ]
};
