import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { UserSession } from '../../auth/user-session';

@Component({
  imports: [],
  selector: 'app-login',
  templateUrl: './login.html',
})
export class Login {
  private readonly router = inject(Router);
  private readonly userSession = inject(UserSession);

  senhaVisivel = false;
  mostrarAjuda = false;

  continuar(event: Event) {
    event.preventDefault();
    this.userSession.start();
    this.router.navigate(['/boas-vindas']);
  }
}
