import {HttpClient} from '@angular/common/http';
import {inject, Injectable, isDevMode} from '@angular/core';
import {getBrowserLang, provideTransloco, Translation, TranslocoLoader} from '@jsverse/transloco';

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
            defaultLang: getBrowserLang() || 'en',
            reRenderOnLangChange: true,
            prodMode: !isDevMode(),
        },
        loader: TranslocoHttpLoader
    })
];
