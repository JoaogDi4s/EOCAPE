import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  imports: [],
  selector: 'app-login',
  templateUrl: './login.html',
})
export class Login {
  private router = inject(Router);

  senhaVisivel = false;
  mostrarAjuda = false;

  continuar(event: Event) {
    event.preventDefault();
    this.router.navigate(['/boas-vindas']);
  }
}
