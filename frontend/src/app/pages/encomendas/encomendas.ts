import { Component } from '@angular/core';
import { PortalList } from '../../components/portal-list/portal-list';
@Component({ selector: 'app-encomendas', imports: [PortalList], templateUrl: './encomendas.html' })
export class EncomendasPage {
  readonly categories = ['Aguardando retirada', 'Retirada'];
  readonly items = [
    {
      title: 'Uma encomenda espera por você',
      description: 'Caixa pequena recebida na portaria. Apresente um documento na retirada.',
      category: 'Aguardando retirada',
      meta: 'Portaria · Unidade 102',
    },
    {
      title: 'Entrega recebida',
      description: 'Envelope entregue ao responsável pela unidade.',
      category: 'Retirada',
      meta: 'Portaria · Unidade 102',
    },
  ];
}
