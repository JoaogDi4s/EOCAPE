import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { BoasVindas } from './pages/boas-vindas/boas-vindas';
import { UserPage } from './pages/user-page/user-page';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'boas-vindas', component: BoasVindas },
  { path: 'user-page', component: UserPage },
  // { path: 'perfil', component: Perfil }
];
