import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { BoasVindas } from './pages/boas-vindas/boas-vindas';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'boas-vindas', component: BoasVindas },
  // { path: 'perfil', component: Perfil }
];
