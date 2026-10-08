import { Component, signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { Footer } from './components/footer/footer';
import { whiteButton } from './components/WhiteButton/whiteButton';

@Component({
  imports: [RouterOutlet, RouterLink,  Navbar, Footer, whiteButton],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',

})
export class App {
  constructor(private router: Router) { }
  protected readonly title = signal('eocape');

  recursos = [
    { titulo: 'Avisos', texto: 'Comunicados do condomínio direto no seu celular.' },
    { titulo: 'Reservas', texto: 'Agende salão de festas, churrasqueira e outros espaços.' },
    { titulo: 'Ocorrências', texto: 'Registre e acompanhe solicitações com a administração.' },
  ];
  irParaLogin() {
    this.router.navigate(['/login']);
  }
}