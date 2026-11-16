import { ConfiguracoesPage } from './pages/configuracoes/configuracoes';
import { requireManager } from './auth/manager-guard';
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
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/administracao/administracao').then((m) => m.Administracao),
        data: { mode: 'dashboard' },
        canActivate: [requireManager],
      },
      {
        path: 'moradores',
        loadComponent: () =>
          import('./pages/administracao/administracao').then((m) => m.Administracao),
        data: { mode: 'moradores' },
        canActivate: [requireManager],
      },
      {
        path: 'estrutura',
        loadComponent: () =>
          import('./pages/administracao/administracao').then((m) => m.Administracao),
        data: { mode: 'estrutura' },
        canActivate: [requireManager],
      },
      {
        path: 'relatos',
        loadComponent: () => import('./pages/relatos/relatos').then((m) => m.RelatosPage),
      },
      {
        path: 'classificados',
        loadComponent: () =>
          import('./pages/classificados/classificados').then((m) => m.ClassificadosPage),
      },
      { path: 'atas', loadComponent: () => import('./pages/atas/atas').then((m) => m.AtasPage) },
      {
        path: 'sugestoes',
        loadComponent: () => import('./pages/sugestoes/sugestoes').then((m) => m.SugestoesPage),
      },
      {
        path: 'encomendas',
        loadComponent: () => import('./pages/encomendas/encomendas').then((m) => m.EncomendasPage),
      },
      {
        path: 'comunicados',
        loadComponent: () =>
          import('./pages/comunicados/comunicados').then((m) => m.ComunicadosPage),
      },
      {
        path: 'reservas',
        loadComponent: () => import('./pages/reservas/reservas').then((m) => m.ReservasPage),
      },
      {
        path: 'manutencoes',
        loadComponent: () =>
          import('./pages/manutencoes/manutencoes').then((m) => m.ManutencoesPage),
      },
      { path: 'configuracoes', component: ConfiguracoesPage },
    ],
  },
  // { path: 'perfil', component: Perfil }
];
