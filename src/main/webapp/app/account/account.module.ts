import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BotsOfflineSharedModule } from '../shared';

import {
    SettingsComponent,
    accountState
} from './';

@NgModule({
    imports: [
        BotsOfflineSharedModule,
        RouterModule.forRoot(accountState, { useHash: true })
    ],
    declarations: [
        SettingsComponent
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BotsOfflineAccountModule {}
