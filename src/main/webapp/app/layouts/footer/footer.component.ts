import { Component } from '@angular/core';
import { VERSION } from '../../app.constants';

@Component({
    selector: 'jhi-footer',
    templateUrl: './footer.component.html'
})
export class FooterComponent {
    version: string;

    constructor() {
        this.version = VERSION;
    }
}
