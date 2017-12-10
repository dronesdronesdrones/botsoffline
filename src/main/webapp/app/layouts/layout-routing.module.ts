import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import {navbarRoute} from '../app.route';
import {errorRoute} from './';
import {ssoRoute} from "./";
import {loginRoute} from "./login/login.route";

const LAYOUT_ROUTES = [
    navbarRoute,
    ssoRoute,
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
