import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { PortalApi } from '../services/portal-api';
export const requireManager: CanActivateFn = () => {
  const router = inject(Router);
  return inject(PortalApi)
    .roles()
    .pipe(
      map(
        (roles) =>
          roles.some((r) => r.ativo && ['SINDICO', 'ADMINISTRADOR'].includes(r.papel)) ||
          router.createUrlTree(['/user-page']),
      ),
      catchError(() => of(router.createUrlTree(['/user-page']))),
    );
};
