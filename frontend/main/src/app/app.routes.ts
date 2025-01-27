import { Routes } from '@angular/router';
import { accessGuard } from './core/guard/access.guard';
import { UserType } from './user/enum/user-type.enum';
import { rootRedirect } from './core/root.redirect';
import { UserLayoutComponent } from './shared/layout/user-layout/user-layout.component';
import { ErrorComponent } from './error/page/error/error.component';
import { RegisterMemberComponent } from './user/page/register-member/register-member.component';
import { ChannelsListComponent } from './channel/page/channels-list/channels-list.component';

export const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: rootRedirect,
      },
      {
        path: 'register',
        component: RegisterMemberComponent,
      },
      {
        path: 'channels',
        component: UserLayoutComponent,
        canMatch: [accessGuard],
        data: {
          allowedUserTypes: [UserType.MEMBER],
        },
        loadChildren: () =>
          import('./channel/channel.routes').then((m) => m.routes),
      },
      {
        path: 'test',
        component: ChannelsListComponent,
      },
      {
        path: 'error',
        loadChildren: () =>
          import('./error/error.routes').then((m) => m.routes),
      },
      {
        path: '**',
        component: ErrorComponent,
        data: {
          titleKey: 'error.page.notFound.title',
        },
      },
    ],
  },
  {
    path: '**',
    component: ErrorComponent,
    data: {
      titleKey: 'error.page.notFound.title',
    },
  },
];
