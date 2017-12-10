import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";
import {ActivatedRoute, Router} from "@angular/router";
import {ConfigService} from "../../shared";

@Component({selector: 'jhi-login', templateUrl: './login.component.html'})
export class LoginComponent implements OnInit {

    constructor(private configService: ConfigService, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.params.subscribe((params) => {
            const target = params['target'];
            this.configService.getSsoUrl().subscribe((url) => {
                if (target) {
                   url = url.replace('uniquestate123', target);
                }
                window.location.href = url;
            });
        });
    }
}
