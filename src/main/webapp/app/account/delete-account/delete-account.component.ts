/*
 * delete-account.component.ts
 *
 * Created on 2017-12-11
 */

import { Component, OnInit } from '@angular/core';

import {Principal, AccountService, LoginService} from '../../shared';
import {Router} from "@angular/router";

@Component({
    selector: 'jhi-delete-account',
    templateUrl: './delete-account.component.html'
})
export class DeleteAccountComponent implements OnInit {
    deleteFailed: boolean;
    account: any;
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
            this.account = this.copyAccount(account);
        });
    }

    copyAccount(account) {
        return {
            login: account.login,
        };
    }

    deleteAccount() {
        this.accountService.delete().subscribe(
                (data) => {
                    this.loginService.logout();
                    this.router.navigate(['']);
                }, (err) => {
                    this.deleteFailed = true;
                }
        );
    }
}
