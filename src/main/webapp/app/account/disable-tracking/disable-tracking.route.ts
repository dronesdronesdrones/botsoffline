/*
 * disable-tracking.route.ts
 *
 * Created on 2017-12-11
 */

import { Route } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import {DisableTrackingComponent} from "./disable-tracking.component";

export const disableTrackingRoute: Route = {
    path: 'disable-tracking',
    component: DisableTrackingComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'Disable Tracking'
    },
    canActivate: [UserRouteAccessService]
};
