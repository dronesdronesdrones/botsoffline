import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';

import {
    BotsOfflineSharedLibsModule,
    BotsOfflineSharedCommonModule,
    CSRFService,
    AuthServerProvider,
    AccountService,
    UserService,
    StateStorageService,
    LoginService,
    Principal,
    HasAnyAuthorityDirective,
    ConfigService
} from './';
import {RouterModule} from "@angular/router";

@NgModule({
    imports: [
        BotsOfflineSharedLibsModule,
        BotsOfflineSharedCommonModule,
        RouterModule
    ],
    declarations: [
        HasAnyAuthorityDirective,
    ],
    providers: [
        LoginService,
        AccountService,
        StateStorageService,
        Principal,
        CSRFService,
        AuthServerProvider,
        UserService,
        DatePipe,
        ConfigService
    ],
    entryComponents: [
    ],
    exports: [
        BotsOfflineSharedCommonModule,
        HasAnyAuthorityDirective,
        DatePipe,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class BotsOfflineSharedModule {}
