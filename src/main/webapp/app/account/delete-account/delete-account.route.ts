/*
 * delete-account.route.ts
 *
 * Created on 2017-12-11
 *
 */

import { Route } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import {DeleteAccountComponent} from "./delete-account.component";

export const deleteAccountRoute: Route = {
    path: 'delete-account',
    component: DeleteAccountComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'Delete Account'
    },
    canActivate: [UserRouteAccessService]
};
