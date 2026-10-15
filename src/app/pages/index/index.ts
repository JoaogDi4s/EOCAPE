import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-index',
  imports: [RouterLink],
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