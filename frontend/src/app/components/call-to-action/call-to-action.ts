import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { whiteButton } from '../WhiteButton/whiteButton';

@Component({
  imports: [whiteButton],
  selector: 'app-call-to-action',
  templateUrl: './call-to-action.html',
})
export class CallToAction {
  constructor(private router: Router) {}
  irParaLogin(): void {
    this.router.navigate(['/login']);
  }
}
