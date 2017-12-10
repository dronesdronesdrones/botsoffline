import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";
import {ActivatedRoute, Router} from "@angular/router";

@Component({selector: 'jhi-login', templateUrl: './login.component.html'})
export class LoginComponent implements OnInit {

    constructor(private http: Http, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.params.subscribe((params) => {
            const target = params['target'];
            this.http.get('api/config/ssourl').subscribe((data) => {
                let url = data.text();
                if (target) {
                   url = url.replace('uniquestate123', target);
                }
                window.location.replace(url);
            });
        });
    }
}
