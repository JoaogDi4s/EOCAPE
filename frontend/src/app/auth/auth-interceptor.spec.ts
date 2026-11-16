import { provideHttpClient, withInterceptors, HttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { authInterceptor } from './auth-interceptor';
import { UserSession } from './user-session';
describe('API authentication', () => {
  let http: HttpTestingController;
  let client: HttpClient;
  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpTestingController);
    client = TestBed.inject(HttpClient);
  });
  afterEach(() => http.verify());
  it('adds tokens only to local API calls', () => {
    TestBed.inject(UserSession).start('test-token');
    client.get('/condominio/meus').subscribe();
    const r = http.expectOne('/condominio/meus');
    expect(r.request.headers.get('Authorization')).toBe('Bearer test-token');
    r.flush([]);
    client.get('https://example.com/data').subscribe();
    const e = http.expectOne('https://example.com/data');
    expect(e.request.headers.has('Authorization')).toBe(false);
    e.flush({});
  });
  it('clears expired sessions on 401', () => {
    const s = TestBed.inject(UserSession);
    s.start('test-token');
    const nav = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    client.get('/condominio/meus').subscribe({ error: () => {} });
    http.expectOne('/condominio/meus').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(s.token).toBeNull();
    expect(nav).toHaveBeenCalledWith(['/login'], { queryParams: { expirada: 1 } });
  });
  it('rejects expired tokens and old demo sessions', () => {
    const s = TestBed.inject(UserSession);
    sessionStorage.setItem('eocape-user-session', 'active');
    expect(s.isAuthenticated()).toBe(false);
    s.start('h.' + btoa(JSON.stringify({ sub: 'u1', fin: 'ACESSO', exp: 1 })) + '.s');
    expect(s.isAuthenticated()).toBe(false);
  });
});
