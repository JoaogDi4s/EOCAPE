import { Component } from '@angular/core';
import { PortalList } from '../../components/portal-list/portal-list';
@Component({
  selector: 'app-comunicados',
  imports: [PortalList],
  templateUrl: './comunicados.html',
})
export class ComunicadosPage {
  readonly categories = ['Importante', 'Convivência', 'Geral'];
  readonly items = [];
}
