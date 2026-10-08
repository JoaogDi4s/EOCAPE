import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { whiteButton } from '../WhiteButton/whiteButton';

@Component({
  imports: [whiteButton],
  selector: 'app-hero',
  templateUrl: './hero.html',
})
export class Hero {
  constructor(private router: Router) {}

  irParaLogin() {
    this.router.navigate(['/login']);
  }
}
