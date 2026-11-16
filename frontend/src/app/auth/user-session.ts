import { Injectable, inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
interface Claims {
  sub: string;
  fin: string;
  exp: number;
}
@Injectable({ providedIn: 'root' })
export class UserSession {
  private readonly storageKey = 'eocape-token';
  get token(): string | null {
    return sessionStorage.getItem(this.storageKey);
  }
  get claims(): Claims | null {
    try {
      const value = this.token?.split('.')[1];
      if (!value) return null;
      return JSON.parse(atob(value.replace(/-/g, '+').replace(/_/g, '/')));
    } catch {
      return null;
    }
  }
  isAuthenticated(): boolean {
    const c = this.claims;
    return !!c && c.fin === 'ACESSO' && c.exp * 1000 > Date.now();
  }
  start(token: string): void {
    sessionStorage.setItem(this.storageKey, token);
  }
  end(): void {
    sessionStorage.removeItem(this.storageKey);
    sessionStorage.removeItem('eocape-user-session');
  }
}
export const requireUserSession: CanActivateFn = () => {
  const session = inject(UserSession);
  return session.isAuthenticated() ? true : inject(Router).createUrlTree(['/login']);
};
