/*
 * delete-account.route.ts
 *
 * Created on 2017-12-11
 *
 */

import { Route } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import {SettingsComponent} from "./settings.component";

export const settingsRoute: Route = {
    path: 'settings',
    component: SettingsComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'Settings'
    },
    canActivate: [UserRouteAccessService]
};
