import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';
import { whiteButton } from '../../components/WhiteButton/whiteButton';

@Component({
  standalone: true,
  selector: 'app-index',
  imports: [RouterLink, Navbar, Footer, whiteButton],
  templateUrl: './index.html',
})

export class Index {
  constructor(private router: Router) { }

  recursos = [
    { titulo: 'Avisos', texto: 'Comunicados do condomínio direto no seu celular.' },
    { titulo: 'Reservas', texto: 'Agende salão de festas, churrasqueira e outros espaços.' },
    { titulo: 'Ocorrências', texto: 'Registre e acompanhe solicitações com a administração.' },
  ];

  irParaLogin() {
    this.router.navigate(['/login']);
  }
}
