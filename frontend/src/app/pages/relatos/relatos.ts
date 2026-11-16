import { Component } from '@angular/core';
import { PortalList } from '../../components/portal-list/portal-list';
@Component({ selector: 'app-relatos', imports: [PortalList], templateUrl: './relatos.html' })
export class RelatosPage {
  readonly categories = ['Aberto', 'Em andamento', 'Resolvido'];
  readonly items = [];
}
