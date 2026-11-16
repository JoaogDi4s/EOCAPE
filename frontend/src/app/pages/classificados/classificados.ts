import { Component } from '@angular/core';
import { PortalList } from '../../components/portal-list/portal-list';
@Component({
  selector: 'app-classificados',
  imports: [PortalList],
  templateUrl: './classificados.html',
})
export class ClassificadosPage {
  readonly categories = ['Venda', 'Doação', 'Serviço'];
  readonly items = [];
}
