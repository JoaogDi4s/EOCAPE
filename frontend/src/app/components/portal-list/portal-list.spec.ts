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
