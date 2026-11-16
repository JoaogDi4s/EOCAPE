import { Component } from '@angular/core';
import { PortalList } from '../../components/portal-list/portal-list';
@Component({
  selector: 'app-manutencoes',
  imports: [PortalList],
  templateUrl: './manutencoes.html',
})
export class ManutencoesPage {
  readonly categories = ['Agendada', 'Em andamento', 'Concluída', 'Cancelada'];
  readonly items = [];
}
