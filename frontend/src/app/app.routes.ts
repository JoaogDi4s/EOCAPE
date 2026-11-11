import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { BoasVindas } from './pages/boas-vindas/boas-vindas';
import { UserPage } from './pages/user-page/user-page';
import { requireUserSession } from './auth/user-session';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'boas-vindas', component: BoasVindas, canActivate: [requireUserSession] },
  { path: 'user-page', component: UserPage, canActivate: [requireUserSession] },
  // { path: 'perfil', component: Perfil }
];
