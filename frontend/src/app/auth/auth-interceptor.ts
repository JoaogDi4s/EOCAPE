import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError, timeout } from 'rxjs';
import { UserSession } from './user-session';
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const session = inject(UserSession),
    router = inject(Router);
  const api =
    /^\/(usuario|condominio|chamado|classificado|manutencao|agendamento-espaco|vinculo-unidade|espaco-comum|area|unidade|arquivo|ata|verificacao-email|api)(\/|$)/.test(
      request.url,
    );
  const login = request.url === '/usuario/login';
  const sent =
    api && !login && session.token
      ? request.clone({ setHeaders: { Authorization: 'Bearer ' + session.token } })
      : request;
  return next(sent).pipe(
    timeout(20000),
    catchError((error: HttpErrorResponse) => {
      if (api && !login && error.status === 401) {
        session.end();
        void router.navigate(['/login'], { queryParams: { expirada: 1 } });
      }
      return throwError(() => error);
    }),
  );
};
