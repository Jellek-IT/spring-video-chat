import {
  ApplicationConfig,
  importProvidersFrom,
  inject,
  Injector,
  LOCALE_ID,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import { DialogService } from 'primeng/dynamicdialog';
import Aura from '@primeng/themes/aura';
import { definePreset } from '@primeng/themes';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { provideKeycloak } from 'keycloak-angular';

import { routes } from './app.routes';
import { LOCATION_INITIALIZED, registerLocaleData } from '@angular/common';
import {
  HttpClient,
  provideHttpClient,
  withInterceptors,
  withInterceptorsFromDi,
} from '@angular/common/http';
import {
  TranslateLoader,
  TranslateModule,
  TranslateService,
} from '@ngx-translate/core';
import { environment } from '../environments/environment';
import { dateInterceptor } from './core/interceptor/date.interceptor';
import { authTokenInterceptor } from './core/interceptor/auth-token.interceptor';
import { ConfirmationService, MessageService } from 'primeng/api';
import { errorInterceptor } from './core/interceptor/error.interceptor';
import localePl from '@angular/common/locales/pl';
import { ProjectConstants } from './shared/constants/project-constants';
(window as any).global = window;

registerLocaleData(localePl, 'pl');

const themePreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '{teal.50}',
      100: '{teal.100}',
      200: '{teal.200}',
      300: '{teal.300}',
      400: '{teal.400}',
      500: '{teal.500}',
      600: '{teal.600}',
      700: '{teal.700}',
      800: '{teal.800}',
      900: '{teal.900}',
      950: '{teal.950}',
    },
    secondary: {
      50: '{purple.50}',
      100: '{purple.100}',
      200: '{purple.200}',
      300: '{purple.300}',
      400: '{purple.400}',
      500: '{purple.500}',
      600: '{purple.600}',
      700: '{purple.700}',
      800: '{purple.800}',
      900: '{purple.900}',
      950: '{purple.950}',
    },
    colorScheme: {
      dark: {
        primary: {
          color: '{teal.400}',
          inverseColor: '{teal.950}',
          hoverColor: '{teal.100}',
          activeColor: '{teal.200}',
        },
        highlight: {
          background: 'rgba(250, 250, 250, .16)',
          focusBackground: 'rgba(250, 250, 250, .24)',
          color: 'rgba(255,255,255,.87)',
          focusColor: 'rgba(255,255,255,.87)',
        },
      },
    },
  },
});

const translateLoaderFactory = (http: HttpClient): TranslateHttpLoader => {
  return new TranslateHttpLoader(http);
};

const initializeTranslation = async () => {
  const translateService = inject(TranslateService);
  const injector = inject(Injector);
  const locationInitialized = await injector.get(
    LOCATION_INITIALIZED,
    Promise.resolve(null)
  );
  await locationInitialized;
  translateService.addLangs([ProjectConstants.DEFAULT_LANGUAGE]);
  translateService.setDefaultLang(ProjectConstants.DEFAULT_LANGUAGE);
  translateService.use(ProjectConstants.DEFAULT_LANGUAGE).subscribe({
    error: () => {
      console.error(
        `Problem with '${ProjectConstants.DEFAULT_LANGUAGE}' language initialization.'`
      );
    },
  });
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: themePreset,
        options: {
          ripple: true,
          darkModeSelector: '.dark-mode',
        },
      },
    }),
    ConfirmationService,
    MessageService,
    DialogService,
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    importProvidersFrom(
      TranslateModule.forRoot({
        loader: {
          provide: TranslateLoader,
          useFactory: translateLoaderFactory,
          deps: [HttpClient],
        },
      })
    ),
    provideAppInitializer(initializeTranslation),
    provideKeycloak({
      config: {
        url: environment.keycloak.url,
        realm: environment.keycloak.realm,
        clientId: environment.keycloak.clientId,
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/silent-check-sso.html',
      },
    }),
    provideHttpClient(
      withInterceptorsFromDi(),
      withInterceptors([
        dateInterceptor,
        authTokenInterceptor,
        errorInterceptor,
      ])
    ),
    { provide: LOCALE_ID, useValue: ProjectConstants.DEFAULT_LANGUAGE },
  ],
};
