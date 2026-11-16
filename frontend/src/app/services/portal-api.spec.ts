import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { TestBed } from '@angular/core/testing';
import { PortalApi } from './portal-api';
describe('PortalApi contracts', () => {
  let api: PortalApi;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(PortalApi);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('uses scoped notices and unwraps pagination', () => {
    let count = 0;
    api.list('avisos', 'c1').subscribe((rows) => (count = rows.length));
    http.expectOne('/condominio/c1/avisos?size=100').flush({ content: [{ titulo: 'Aviso' }] });
    expect(count).toBe(1);
  });
  it('uses the existing minutes route', () => {
    api.list('atas', 'c1').subscribe();
    http.expectOne('/api/condominios/c1/atas').flush([]);
  });
  it('posts report payloads to the existing endpoint', () => {
    const body = { unidadeId: 'u1', titulo: 'Lâmpada', descricao: 'Garagem', categoria: 'OUTROS' };
    api.create('chamado', body).subscribe();
    const req = http.expectOne('/chamado');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush({});
  });
});
