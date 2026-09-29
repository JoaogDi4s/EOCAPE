import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';

@Component({
  selector: 'app-index',
  imports: [RouterLink, Navbar, Footer],
  templateUrl: './index.html',
  styleUrl: './index.css',
})
export class Index {
  recursos = [
    { titulo: 'Avisos', texto: 'Comunicados do condomínio direto no seu celular.' },
    { titulo: 'Reservas', texto: 'Agende salão de festas, churrasqueira e outros espaços.' },
    { titulo: 'Ocorrências', texto: 'Registre e acompanhe solicitações com a administração.' },
  ];
}
