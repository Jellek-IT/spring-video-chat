import { Routes } from '@angular/router';
import { ErrorComponent } from './page/error/error.component';

export const routes: Routes = [
	{
		path: 'bad-gateway',
		component: ErrorComponent,
		data: {
			titleKey: 'error.page.badGateway.title'
		}
	},
	{
		path: 'service-unavailable',
		component: ErrorComponent,
		data: {
			titleKey: 'error.page.serviceUnavailable.title'
		}
	},
	{
		path: 'timeout',
		component: ErrorComponent,
		data: {
			titleKey: 'error.page.timeout.title'
		}
	}
];
