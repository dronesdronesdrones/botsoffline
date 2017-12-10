import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';

import { Account, Principal } from '../shared';
import {Http} from "@angular/http";

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: [
        'home.css'
    ]
})
export class HomeComponent implements OnInit {
    account: Account;
    status: string;
    system: string;
    totalMinutes: number;

    constructor(
        private principal: Principal,
        private eventManager: JhiEventManager,
        private http: Http
    ) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
            this.loadStats();
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.principal.identity().then((account) => {
                this.account = account;
                this.loadStats();
            });
        });
    }

    loadStats() {
        this.http.get("/api/player/current-status").subscribe(
                (data) => {
                    const status = data.status;
                    console.log(data.json());
                    if (status === 200) {
                        this.status = 'active';
                        this.system = data.json().name;
                    } else if (status === 204) {
                        this.status = 'inactive';
                    }
                },
                (err) => {
                    alert("Failed to load your user. Please submit a ticket if this persists.")
                }
        );
        this.http.get("/api/player/total-minutes").subscribe(
                (data) => {
                    this.totalMinutes = +data.text();
                },
                (err) => {
                    alert("Failed to load your user. Please submit a ticket if this persists.")
                }
        );
    }


    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

}
