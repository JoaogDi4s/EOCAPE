import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { Login } from './login';
import { UserSession } from '../../auth/user-session';
describe('Login API', () => {
  let http: HttpTestingController;
  let component: Login;
  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      imports: [Login],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
    component = TestBed.createComponent(Login).componentInstance;
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('explains password requirements before calling the API', () => {
    component.step = 'senha';
    component.novaSenha = 'curta';
    component.confirmacaoSenha = 'curta';
    component.primeiroAcesso();
    expect(component.error).toContain('8 e 72');
    http.expectNone('/usuario/definir-senha');
  });
  it('requires six digits before verifying the code', () => {
    component.step = 'codigo';
    component.codigo = '12';
    component.primeiroAcesso();
    expect(component.error).toContain('6 números');
    http.expectNone('/verificacao-email/verificar');
  });
  it('only authenticates after the API accepts credentials', () => {
    const nav = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    component.cpf = '123.456.789-00';
    component.senha = 'teste';
    component.continuar(new Event('submit'));
    expect(TestBed.inject(UserSession).isAuthenticated()).toBe(false);
    const req = http.expectOne('/usuario/login');
    expect(req.request.body).toEqual({ cpf: '12345678900', senha: 'teste' });
    req.flush({
      token:
        'h.' +
        btoa(JSON.stringify({ sub: 'u1', fin: 'ACESSO', exp: Date.now() / 1000 + 60 })) +
        '.s',
      trocaSenhaObrigatoria: false,
    });
    expect(TestBed.inject(UserSession).isAuthenticated()).toBe(true);
    expect(nav).toHaveBeenCalledWith(['/boas-vindas']);
  });
  it('shows authentication errors without opening the panel', () => {
    component.cpf = '00000000000';
    component.senha = 'teste';
    component.continuar(new Event('submit'));
    http.expectOne('/usuario/login').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(component.error).toContain('CPF ou senha');
    expect(component.busy).toBe(false);
    expect(TestBed.inject(UserSession).isAuthenticated()).toBe(false);
  });
  it('keeps first access outside the normal panel', () => {
    component.cpf = '00000000000';
    component.senha = 'teste';
    component.continuar(new Event('submit'));
    http.expectOne('/usuario/login').flush({
      token:
        'h.' +
        btoa(JSON.stringify({ sub: 'u1', fin: 'PRIMEIRO_ACESSO', exp: Date.now() / 1000 + 60 })) +
        '.s',
      trocaSenhaObrigatoria: true,
    });
    expect(component.step).toBe('email');
    expect(TestBed.inject(UserSession).isAuthenticated()).toBe(false);
  });
});
