import { Component } from '@angular/core';
import { PortalList } from '../../components/portal-list/portal-list';
@Component({ selector: 'app-atas', imports: [PortalList], templateUrl: './atas.html' })
export class AtasPage {
  readonly categories = ['Publicada', 'Rascunho'];
  readonly items = [];
}
