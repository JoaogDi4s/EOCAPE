import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { PortalList } from './portal-list';
describe('Ticket photo workflow', () => {
  beforeEach(() => {
    vi.stubGlobal(
      'URL',
      class extends URL {
        static override createObjectURL = vi.fn(() => 'blob:preview');
        static override revokeObjectURL = vi.fn();
      },
    );
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
  });
  afterEach(() => vi.unstubAllGlobals());
  function setup() {
    const fixture = TestBed.createComponent(PortalList);
    fixture.componentRef.setInput('title', 'Relatos');
    fixture.componentRef.setInput('description', 'Solicitações');
    fixture.detectChanges();
    fixture.componentRef.setInput('resource', 'chamado');
    const page = fixture.componentInstance;
    page.condoId = 'c1';
    page.optionId = 'u1';
    page.draftTitle = 'Lâmpada';
    page.draftDescription = 'Lâmpada do corredor piscando';
    return { fixture, page, http: TestBed.inject(HttpTestingController) };
  }
  function event(files: File[]) {
    return { target: { files, value: 'selected' } } as unknown as Event;
  }
  it('downloads the signed file as a blob with its original filename', () => {
    vi.useFakeTimers();
    const { page, http } = setup();
    let filename = '';
    const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(function (
      this: HTMLAnchorElement,
    ) {
      filename = this.download;
    });
    page.download({ id: 'v1', arquivoId: 'f1', nomeOriginal: 'ata.pdf' });
    http
      .expectOne('/arquivo/f1/download')
      .flush({ url: 'https://storage.example.test/signed-file' });
    const request = http.expectOne('https://storage.example.test/signed-file');
    expect(request.request.responseType).toBe('blob');
    request.flush(new Blob(['pdf'], { type: 'application/pdf' }));
    expect(filename).toBe('ata.pdf');
    expect(page.downloading()).toBe('');
    expect(page.feedback()).toContain('Download iniciado');
    vi.advanceTimersByTime(30000);
    expect(URL.revokeObjectURL).toHaveBeenCalled();
    click.mockRestore();
    vi.useRealTimers();
    http.verify();
  });
  it('rejects non-images and oversized photos before uploading', () => {
    const { page, http } = setup();
    page.addPhotos(event([new File(['pdf'], 'teste.pdf', { type: 'application/pdf' })]));
    expect(page.photos()).toHaveLength(0);
    expect(page.error()).toContain('imagem');
    page.addPhotos(
      event([
        new File([new Uint8Array(10 * 1024 * 1024 + 1)], 'grande.jpg', { type: 'image/jpeg' }),
      ]),
    );
    expect(page.photos()).toHaveLength(0);
    expect(page.error()).toContain('10 MB');
    http.verify();
  });
  it('limits selection and releases removed previews', () => {
    const { page } = setup();
    page.addPhotos(
      event(
        Array.from(
          { length: 6 },
          (_, i) => new File(['image'], i + '.jpg', { type: 'image/jpeg' }),
        ),
      ),
    );
    expect(page.photos()).toHaveLength(5);
    expect(page.error()).toContain('5 fotos');
    page.removePhoto(page.photos()[0]);
    expect(URL.revokeObjectURL).toHaveBeenCalledWith('blob:preview');
    expect(page.photos()).toHaveLength(4);
  });
  it('retries a failed attachment without recreating the ticket or reuploading the file', () => {
    const { page, http } = setup();
    page.composing.set(true);
    page.addPhotos(event([new File(['image'], 'foto.jpg', { type: 'image/jpeg' })]));
    page.save();
    http
      .expectOne('/chamado')
      .flush({ id: 't1', titulo: 'Lâmpada', descricao: 'Corredor', status: 'ABERTO' });
    const upload = http.expectOne('/arquivo/upload?condominioId=c1&tipo=CHAMADO');
    expect(upload.request.body.get('arquivo').name).toBe('foto.jpg');
    upload.flush({ id: 'f1' });
    http.expectOne('/chamado/t1/arquivos').flush({}, { status: 503, statusText: 'Unavailable' });
    expect(page.savedTicket()?.id).toBe('t1');
    expect(page.photos()[0].state).toBe('failed');
    expect(page.saving()).toBe(false);
    page.save();
    http.expectNone('/chamado');
    http.expectNone('/arquivo/upload?condominioId=c1&tipo=CHAMADO');
    http.expectOne('/chamado/t1/arquivos').flush({ id: 'v1' });
    expect(page.composing()).toBe(false);
    expect(page.photos()).toHaveLength(0);
    expect(page.serverItems()).toHaveLength(1);
    http.verify();
  });
});
