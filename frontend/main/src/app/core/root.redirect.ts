import { inject } from '@angular/core';
import { RedirectFunction } from '@angular/router';
import Keycloak from 'keycloak-js';

export const rootRedirect: RedirectFunction = () => {
	const keycloak = inject(Keycloak);
	const authenticated = keycloak?.authenticated ?? false;
	return authenticated ? '/channels' : '/register';
};
