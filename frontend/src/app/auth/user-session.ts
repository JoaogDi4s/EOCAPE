import { Injectable, inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class UserSession {
  private readonly storageKey = 'eocape-user-session';

  isAuthenticated(): boolean {
    return sessionStorage.getItem(this.storageKey) === 'active';
  }

  start(): void {
    sessionStorage.setItem(this.storageKey, 'active');
  }

  end(): void {
    sessionStorage.removeItem(this.storageKey);
  }
}

export const requireUserSession: CanActivateFn = () => {
  const session = inject(UserSession);
  const router = inject(Router);

  return session.isAuthenticated() ? true : router.createUrlTree(['/login']);
};
