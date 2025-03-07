import { TranslocoTestingModule, TranslocoTestingOptions, TRANSLOCO_TRANSPILER } from '@jsverse/transloco';
import en from '../../../assets/i18n/en.json';

export function getTranslocoModule(options: TranslocoTestingOptions = {}) {
  return TranslocoTestingModule.forRoot({
    langs: { en },
    translocoConfig: {
      availableLangs: ['en'],
      defaultLang: 'en',
      reRenderOnLangChange: true,
      transpiler: { interpolate: (value: string) => value }
    },
    preloadLangs: true,
    ...options
  });
}
