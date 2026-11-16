import { Component } from '@angular/core';
import { PortalList } from '../../components/portal-list/portal-list';
@Component({ selector: 'app-reservas', imports: [PortalList], templateUrl: './reservas.html' })
export class ReservasPage {
  readonly categories = ['Pendente', 'Confirmado', 'Concluído', 'Cancelado'];
  readonly items = [];
}
