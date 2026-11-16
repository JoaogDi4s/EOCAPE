import { ConfiguracoesPage } from './pages/configuracoes/configuracoes';
import { ManutencoesPage } from './pages/manutencoes/manutencoes';
import { ReservasPage } from './pages/reservas/reservas';
import { ComunicadosPage } from './pages/comunicados/comunicados';
import { EncomendasPage } from './pages/encomendas/encomendas';
import { SugestoesPage } from './pages/sugestoes/sugestoes';
import { AtasPage } from './pages/atas/atas';
import { ClassificadosPage } from './pages/classificados/classificados';
import { RelatosPage } from './pages/relatos/relatos';
import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { BoasVindas } from './pages/boas-vindas/boas-vindas';
import { UserPage } from './pages/user-page/user-page';
import { requireUserSession } from './auth/user-session';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'boas-vindas', component: BoasVindas, canActivate: [requireUserSession] },
  {
    path: 'user-page',
    component: UserPage,
    canActivate: [requireUserSession],
    children: [
      { path: 'relatos', component: RelatosPage },
      { path: 'classificados', component: ClassificadosPage },
      { path: 'atas', component: AtasPage },
      { path: 'sugestoes', component: SugestoesPage },
      { path: 'encomendas', component: EncomendasPage },
      { path: 'comunicados', component: ComunicadosPage },
      { path: 'reservas', component: ReservasPage },
      { path: 'manutencoes', component: ManutencoesPage },
      { path: 'configuracoes', component: ConfiguracoesPage },
    ],
  },
  // { path: 'perfil', component: Perfil }
];
