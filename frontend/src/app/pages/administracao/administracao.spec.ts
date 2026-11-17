import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { Administracao } from './administracao';

describe('Administração', () => {
  it('links existing residents without submitting a new password or overwriting identity', () => {
    const { page, http } = setup('moradores');
    http.expectOne('/condominio/meus').flush([{ id: 'c1', nome: 'Condomínio' }]);
    http
      .expectOne('/papel-condominio/meus')
      .flush([{ condominioId: 'c1', papel: 'ADMINISTRADOR', ativo: true }]);
    http.match((r) => r.method === 'GET').forEach((r) => r.flush([]));
    page.existingResident = true;
    page.composing = true;
    page.cpf = '00000000000';
    page.email = 'pessoa@example.test';
    page.unitId = 'u1';
    page.createResident();
    const req = http.expectOne('/condominio/c1/moradores/vincular');
    expect(req.request.body).toEqual({
      cpf: '00000000000',
      email: 'pessoa@example.test',
      unidadeId: 'u1',
      tipoVinculo: 'INQUILINO',
    });
    req.flush(
      { mensagem: 'Este morador já possui vínculo ativo com a unidade selecionada.' },
      { status: 409, statusText: 'Conflict' },
    );
    expect(page.composing).toBe(true);
    expect(page.cpf).toBe('00000000000');
    expect(page.error()).toContain('vínculo ativo');
    http.verify();
  });
  function setup(mode = 'dashboard') {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { data: { mode } } } },
      ],
    });
    const fixture = TestBed.createComponent(Administracao),
      http = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    return { fixture, http, page: fixture.componentInstance };
  }
  it('only offers condominiums managed by the user', () => {
    const { page, http } = setup();
    http.expectOne('/condominio/meus').flush([
      { id: 'c1', nome: 'Morador' },
      { id: 'c2', nome: 'Inativo' },
    ]);
    http
      .expectOne('/papel-condominio/meus')
      .flush([{ condominioId: 'c2', papel: 'ADMINISTRADOR', ativo: false }]);
    expect(page.condos()).toEqual([]);
    expect(page.loading()).toBe(false);
    http.verify();
  });
  it('marks failed dashboard metrics unavailable rather than presenting zero', () => {
    const { page, http } = setup();
    http.expectOne('/condominio/meus').flush([{ id: 'c1', nome: 'Condomínio' }]);
    http
      .expectOne('/papel-condominio/meus')
      .flush([{ condominioId: 'c1', papel: 'ADMINISTRADOR', ativo: true }]);
    http
      .expectOne('/condominio/c1/moradores')
      .flush([], { status: 503, statusText: 'Unavailable' });
    http.expectOne('/chamado/condominio/c1').flush([{ status: 'ABERTO' }, { status: 'RESOLVIDO' }]);
    http
      .expectOne('/agendamento-espaco/condominio/c1')
      .flush([{ status: 'PENDENTE' }, { status: 'CANCELADO' }]);
    http.expectOne('/manutencao/condominio/c1').flush([]);
    http.expectOne('/api/condominios/c1/atas').flush([]);
    http.expectOne('/condominio/c1/avisos?size=100').flush({ content: [] });
    http.expectOne('/classificado/condominio/c1').flush([]);
    expect(page.unavailableMetric('Moradores')).toBe(true);
    expect(page.pendingTickets().length).toBe(1);
    expect(page.resolutionRate()).toBe(50);
    expect(page.pendingReservations().length).toBe(1);
    http.verify();
  });
});
