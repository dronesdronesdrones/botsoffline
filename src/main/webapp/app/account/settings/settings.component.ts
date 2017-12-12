/*
 * delete-account.component.ts
 *
 * Created on 2017-12-11
 */

import { Component, OnInit } from '@angular/core';

import {Principal, AccountService, LoginService} from '../../shared';
import {Router} from "@angular/router";

@Component({
    selector: 'jhi-settings',
    templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {

    account: any;

    changeTrackingSuccess: boolean;
    changeTrackingSubmitted: boolean;

    changeLeaderboardSuccess: boolean;
    changeLeaderboardSubmitted: boolean;

    accountDeleteFailed: boolean;
    characterNameConfirm: string;

    constructor(
        private accountService: AccountService,
        private principal: Principal,
        private loginService: LoginService,
        private router: Router
    ) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
        });
    }

    deleteAccount() {
        this.accountService.delete().subscribe(
                (data) => {
                    this.loginService.logout();
                    this.router.navigate(['']);
                }, (err) => {
                    this.accountDeleteFailed = true;
                }
        );
    }

    changeTracking(status: string): void {
        this.accountService.changeTracking(status).subscribe((data) => {
            this.changeTrackingSuccess = true
            this.changeTrackingSubmitted = true;
            if (status === 'ENABLED') {
                this.account.trackingStatus = 'ENABLED';
            } else {
                this.account.trackingStatus = 'DISABLED';
            }
        }, (err) => {
            this.changeTrackingSuccess = false
            this.changeTrackingSubmitted = true;
        });
    }

    hide(hide: boolean): void {
        this.accountService.hideFromLeaderboard(hide).subscribe((data) => {
            this.changeLeaderboardSuccess = true
            this.changeLeaderboardSubmitted = true;
            this.account.hideFromLeaderboard = hide;
        }, (err) => {
            this.changeLeaderboardSuccess = false
            this.changeLeaderboardSubmitted = true;
        });
    }
}
