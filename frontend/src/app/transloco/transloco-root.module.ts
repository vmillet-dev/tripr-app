import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode, inject } from '@angular/core';
import {
  Translation,
  TranslocoLoader,
  provideTransloco
} from '@jsverse/transloco';

@Injectable({ providedIn: 'root' })
export class TranslocoHttpLoader implements TranslocoLoader {
  private http = inject(HttpClient);

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
