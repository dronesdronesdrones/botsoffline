import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';

import {Account, ConfigService, Principal} from '../shared';
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
    system: any;
    totalMinutes: number;
    ssoUrl: string;
    rankInSystem: number;
    candidates: any[];
    highscore: any[];
    myScore: number;

    localScan: string;
    localScanSubmitSuccessful: boolean;
    awardedLocalScanPoints: boolean;

    constructor(
        private principal: Principal,
        private eventManager: JhiEventManager,
        private http: Http,
        private configService: ConfigService
    ) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
            if (account) {
                this.loadStats();
                this.http.get('api/score/days/7').subscribe((data) => this.myScore = +data.text());
            }
        });
        this.registerAuthenticationSuccess();
        this.configService.getSsoUrl().subscribe((data) => this.ssoUrl = data);
        this.http.get('api/candidates').subscribe((data) => this.candidates = data.json());
        this.http.get('api/highscore/days/7/length/10').subscribe((data) => this.highscore = data.json());
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.principal.identity().then((account) => {
                this.account = account;
                if (account) {
                    this.loadStats();
                }
            });
        });
    }

    loadStats() {
        this.http.get("/api/player/current-status").subscribe(
                (data) => {
                    const status = data.status;
                    if (status === 200) {
                        this.system = data.json();
                        if (this.system.inOwnSov) {
                            this.status = 'inactive';
                        } else {
                            this.status = 'active';
                        }
                    } else if (status === 204) {
                        this.status = 'inactive';
                    }
                }
        );
        this.http.get("/api/player/total-minutes").subscribe(
                (data) => {
                    this.totalMinutes = +data.text();
                }
        );
        this.http.get("/api/player/rank-in-system").subscribe(
                (data) => {
                    if (data.status === 200) {
                        this.rankInSystem = +data.text();
                    }
                }
        );
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    submitLocalScan() {
        this.awardedLocalScanPoints = false;
        this.localScanSubmitSuccessful = false;
        if (this.localScan) {
            this.http.post('api/local-scan', this.localScan).subscribe((data) => {
                this.localScan = null;
                this.localScanSubmitSuccessful = true;
                if (data.status === 201) {
                    this.awardedLocalScanPoints = true;
                }
            }, (err) => alert('Failed to submit local scan. Please try again later.'));
        }
    }

}
