import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import {
  TRANSLOCO_LOADER,
  Translation,
  TranslocoLoader,
  TRANSLOCO_CONFIG,
  translocoConfig,
  TranslocoModule,
  provideTransloco
} from '@ngneat/transloco';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TranslocoHttpLoader implements TranslocoLoader {
  constructor(private http: HttpClient) {}

  getTranslation(lang: string) {
    return this.http.get<Translation>(`/assets/i18n/${lang}.json`);
  }
}

export const translocoProviders = [
  provideTransloco({
    config: {
      availableLangs: ['en', 'fr'],
      defaultLang: 'en',
      reRenderOnLangChange: true,
      prodMode: !isDevMode(),
    },
    loader: TranslocoHttpLoader
  })
];

// Keep this for backward compatibility
export class TranslocoRootModule {}
