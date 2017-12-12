import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

@Injectable()
export class AccountService  {
    constructor(private http: Http) { }

    get(): Observable<any> {
        return this.http.get('api/account').map((res: Response) => res.json());
    }

    changeTracking(status: string): Observable<any> {
        return this.http.put('api/account/tracking/' + status, null);
    }

    delete() {
        return this.http.delete('api/account');
    }

    hideFromLeaderboard(hide: boolean) {
        return this.http.put('api/account/leaderboard/' + hide, null);
    }
}
