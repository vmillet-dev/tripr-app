import {ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {routes} from './app.routes';
import {translocoProviders} from './transloco/transloco-root.module';
import {AuthService} from './core/services/auth.service';
import {authInterceptor} from './core/interceptors/auth.interceptor';
import {firstValueFrom} from "rxjs";

export const appConfig: ApplicationConfig = {
    providers: [
        provideZoneChangeDetection({eventCoalescing: true}),
        provideRouter(routes),
        provideHttpClient(withInterceptors([authInterceptor])),
        ...translocoProviders,
        provideAppInitializer(async () => {
            const auth = inject(AuthService);
            return firstValueFrom(auth.refreshToken()).catch(() => {
                return false;
            });
        })
    ]
};
