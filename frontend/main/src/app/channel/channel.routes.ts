import { Routes } from '@angular/router';
import { ChannelsListComponent } from './page/channels-list/channels-list.component';
import { ChannelDetailsComponent } from './page/channel-details/channel-details.component';

export const routes: Routes = [
  {
    path: '',
    component: ChannelsListComponent,
  },
  {
    path: ':id/details',
    component: ChannelDetailsComponent,
  },
];
