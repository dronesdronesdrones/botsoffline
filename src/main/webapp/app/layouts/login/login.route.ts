import {Routes} from '@angular/router';
import {LoginComponent} from "./login.component";

export const loginRoute: Routes = [{
    path: 'login/:target', component: LoginComponent
}, {
    path: 'login', component: LoginComponent
}];
