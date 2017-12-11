/*
 * disable-tracking.component.ts
 *
 * Created on 2017-12-11
 */

import {Component, OnInit} from '@angular/core';

import {Principal, AccountService} from '../../shared';

@Component({
               selector: 'jhi-disable-tracking',
               templateUrl: './disable-tracking.component.html'
           })
export class DisableTrackingComponent implements OnInit {
    success: boolean;
    submitted: boolean;
    account: any;

    constructor(private accountService: AccountService, private principal: Principal) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
        });
    }

    changeTracking(status: string): void {
        this.accountService.changeTracking(status).subscribe((data) => {
            this.success = true
            this.submitted = true;
            if (status === 'ENABLED') {
                this.account.trackingStatus = 'ENABLED';
            } else {
                this.account.trackingStatus = 'DISABLED';
            }
        }, (err) => {
            this.success = false
            this.submitted = true;
        });
    }
}
