import {Component} from '@angular/core';
import {RouterLink} from '@angular/router';
import {TranslocoModule} from '@jsverse/transloco';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  imports: [RouterLink, TranslocoModule]
})
export class HomeComponent { }
