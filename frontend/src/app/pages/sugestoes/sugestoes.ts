import { Component } from '@angular/core';
import { PortalList } from '../../components/portal-list/portal-list';
@Component({ selector: 'app-sugestoes', imports: [PortalList], templateUrl: './sugestoes.html' })
export class SugestoesPage {
  readonly categories = ['Convivência', 'Sustentabilidade', 'Melhorias'];
  readonly items = [
    {
      title: 'Um cantinho de leitura',
      description: 'Que tal uma pequena biblioteca compartilhada no hall?',
      category: 'Convivência',
      meta: 'Ideia da comunidade',
    },
    {
      title: 'Coleta de óleo de cozinha',
      description: 'Um ponto de coleta para descarte consciente.',
      category: 'Sustentabilidade',
      meta: 'Ideia da comunidade',
    },
  ];
}
