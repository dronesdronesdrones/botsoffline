import { Routes } from '@angular/router';

import {
    disableTrackingRoute,
    deleteAccountRoute
} from './';

const ACCOUNT_ROUTES = [
    disableTrackingRoute,
    deleteAccountRoute
];

export const accountState: Routes = [{
    path: '',
    children: ACCOUNT_ROUTES
}];
