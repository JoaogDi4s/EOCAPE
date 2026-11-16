import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PortalList } from './portal-list';

describe('PortalList', () => {
  function setup() {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    const fixture = TestBed.createComponent(PortalList);
    fixture.componentRef.setInput('title', 'Relatos');
    fixture.componentRef.setInput('description', 'Solicitações');
    fixture.componentRef.setInput('categories', ['Aberto', 'Resolvido']);
    fixture.componentRef.setInput('items', [
      { title: 'Lâmpada', description: 'Garagem', category: 'Aberto', meta: 'Bloco A' },
      { title: 'Portão', description: 'Entrada', category: 'Resolvido', meta: 'Bloco B' },
    ]);
    fixture.detectChanges();
    return fixture;
  }

  it('combines search and category filters and renders the empty state', () => {
    const fixture = setup();
    fixture.componentInstance.query.set('GARAGEM');
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.record').length).toBe(1);
    fixture.componentInstance.filter.set('Resolvido');
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.empty')).toBeTruthy();
  });
  it('shows administrative creation only in the condominium managed by the user', () => {
    const fixture = setup(),
      page = fixture.componentInstance;
    fixture.componentRef.setInput('resource', 'atas');
    page.condoId = 'c1';
    page.roles.set([{ condominioId: 'c2', papel: 'ADMINISTRADOR', ativo: true }]);
    expect(page.canCreate).toBe(false);
    page.roles.set([{ condominioId: 'c1', papel: 'ADMINISTRADOR', ativo: true }]);
    expect(page.canCreate).toBe(true);
  });
  it('uses the singular attachment endpoint required by minutes', () => {
    const fixture = setup(),
      page = fixture.componentInstance;
    fixture.componentRef.setInput('resource', 'atas');
    page.openDetails({
      title: 'Assembleia',
      description: 'Decisões',
      category: 'Publicada',
      meta: '',
      data: { id: 'a1', publicada: true },
    });
    const http = TestBed.inject(HttpTestingController);
    http
      .expectOne('/ata/a1/arquivo')
      .flush([{ id: 'v1', arquivoId: 'f1', nomeOriginal: 'ata.pdf' }]);
    expect(page.attachments()[0].arquivoId).toBe('f1');
    http.verify();
  });
  it('submits reservation approval using the selected record id', () => {
    const fixture = setup(),
      page = fixture.componentInstance;
    fixture.componentRef.setInput('resource', 'agendamento-espaco');
    page.condoId = 'c1';
    page.selected.set({
      title: 'Salão',
      description: 'Encontro',
      category: 'Pendente',
      meta: '',
      data: { id: 'r1', status: 'PENDENTE' },
    });
    page.change('aprovar');
    const http = TestBed.inject(HttpTestingController),
      request = http.expectOne('/agendamento-espaco/r1/aprovar');
    expect(request.request.method).toBe('PATCH');
    request.flush({});
    http.expectOne('/agendamento-espaco/condominio/c1').flush([]);
    expect(page.feedback()).toContain('sucesso');
    http.verify();
  });

  it('requires a date for reservations and shows the new local record', () => {
    const fixture = setup();
    fixture.componentRef.setInput('reservation', true);
    const page = fixture.componentInstance;
    page.openForm();
    page.draftTitle = 'Salão de festas';
    page.draftDescription = 'Encontro da família';
    page.save();
    expect(page.added().length).toBe(0);
    page.draftDate = '2026-12-20';
    page.save();
    fixture.detectChanges();
    expect(page.added()[0].meta).toContain('20/12/2026');
    expect(fixture.nativeElement.querySelector('[role="status"]')).toBeTruthy();
  });
});
