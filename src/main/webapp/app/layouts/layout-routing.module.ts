import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import {navbarRoute} from '../app.route';
import {errorRoute} from './';
import {ssoRoute} from "./";
import {loginRoute} from "./login/login.route";
import {legalRoute} from "./legal/legal.route";

const LAYOUT_ROUTES = [
    navbarRoute,
    ssoRoute,
    legalRoute,
    ...loginRoute,
    ...errorRoute
];

@NgModule({
    imports: [
        RouterModule.forRoot(LAYOUT_ROUTES, { useHash: true })
    ],
    exports: [
        RouterModule
    ]
})
export class LayoutRoutingModule {}
