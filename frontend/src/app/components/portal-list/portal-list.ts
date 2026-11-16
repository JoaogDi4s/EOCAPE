import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { forkJoin, finalize, switchMap, from, concatMap, catchError, of, toArray } from 'rxjs';
import {
  PortalApi,
  Condo,
  Option,
  RecordData,
  Attachment,
  category,
} from '../../services/portal-api';
import { UserSession } from '../../auth/user-session';
import { apiError } from '../../services/api-error';
import {
  afterNextRender,
  Component,
  ElementRef,
  Injector,
  input,
  signal,
  computed,
  inject,
  OnInit,
  DestroyRef,
  viewChild,
  viewChildren,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
export interface DraftPhoto {
  key: number;
  file: File;
  preview: string;
  state: 'pending' | 'sending' | 'sent' | 'failed';
  uploadedId?: string;
  error?: string;
}
export interface Item {
  data?: RecordData;
  title: string;
  description: string;
  category: string;
  meta: string;
}
@Component({
  selector: 'app-portal-list',
  imports: [FormsModule],
  templateUrl: './portal-list.html',
  styleUrl: './portal-list.css',
})
export class PortalList implements OnInit {
  private injector = inject(Injector);
  private viewHeading = viewChild<ElementRef<HTMLElement>>('viewHeading');
  private createButton = viewChild<ElementRef<HTMLButtonElement>>('createButton');
  private detailButtons = viewChildren<ElementRef<HTMLButtonElement>>('detailButton');
  private focusView(item?: Item) {
    afterNextRender(
      () => {
        const index = item ? this.visible().indexOf(item) : -1;
        const target = this.viewHeading() ?? this.detailButtons()[index] ?? this.createButton();
        target?.nativeElement.focus();
      },
      { injector: this.injector },
    );
  }
  closeDetails() {
    const item = this.selected();
    this.selected.set(null);
    this.focusView(item ?? undefined);
  }
  readonly titleExamples: Record<string, string> = {
    chamado: 'Ex.: Lâmpada queimada na garagem',
    classificado: 'Ex.: Bicicleta infantil à venda',
    manutencao: 'Ex.: Inspeção dos elevadores',
    atas: 'Ex.: Assembleia de novembro',
    avisos: 'Ex.: Limpeza da caixa-d’água',
  };
  photos = signal<DraftPhoto[]>([]);
  downloading = signal('');
  private downloadUrls = new Set<string>();
  savedTicket = signal<RecordData | null>(null);
  private photoSequence = 0;
  constructor() {
    inject(DestroyRef).onDestroy(() => {
      this.clearPhotos();
      this.downloadUrls.forEach((url) => URL.revokeObjectURL(url));
    });
  }
  private clearPhotos() {
    this.photos().forEach((p) => URL.revokeObjectURL(p.preview));
    this.photos.set([]);
  }
  addPhotos(event: Event) {
    if (this.saving()) return;
    const input = event.target as HTMLInputElement;
    const files = Array.from(input.files || []);
    this.error.set('');
    const problems: string[] = [];
    for (const file of files) {
      if (this.photos().length >= 5) {
        problems.push('Você pode adicionar até 5 fotos por chamado.');
        break;
      }
      if (!file.type.startsWith('image/')) {
        problems.push('Escolha uma imagem da galeria ou tire uma foto.');
        continue;
      }
      if (!file.size || file.size > 10 * 1024 * 1024) {
        problems.push('Cada foto precisa ter conteúdo e no máximo 10 MB.');
        continue;
      }
      this.photos.update((rows) => [
        ...rows,
        { key: ++this.photoSequence, file, preview: URL.createObjectURL(file), state: 'pending' },
      ]);
    }
    input.value = '';
    this.error.set([...new Set(problems)].join(' '));
  }
  removePhoto(photo: DraftPhoto) {
    if (this.saving() || photo.state === 'sent') return;
    URL.revokeObjectURL(photo.preview);
    this.photos.update((rows) => rows.filter((p) => p.key !== photo.key));
  }
  cancelForm() {
    if (this.saving()) return;
    const record = this.savedTicket();
    this.composing.set(false);
    this.focusView();
    this.clearPhotos();
    this.savedTicket.set(null);
    this.error.set('');
    if (record) {
      this.openDetails(this.toItem(record));
      this.feedback.set('Seu chamado está salvo. Você pode adicionar mais fotos nos detalhes.');
    }
  }
  private sendPhotos(record: RecordData) {
    if (!record.id) return of([]);
    const update = (key: number, values: Partial<DraftPhoto>) =>
      this.photos.update((rows) => rows.map((p) => (p.key === key ? { ...p, ...values } : p)));
    return from(this.photos().filter((p) => p.state !== 'sent')).pipe(
      concatMap((photo) => {
        update(photo.key, { state: 'sending', error: undefined });
        const upload = photo.uploadedId
          ? of({ id: photo.uploadedId })
          : this.api.upload(this.condoId, 'CHAMADO', photo.file);
        return upload.pipe(
          switchMap((file) => {
            update(photo.key, { uploadedId: file.id });
            return this.api.post('/chamado/' + record.id + '/arquivos', { arquivoId: file.id });
          }),
          switchMap(() => {
            update(photo.key, { state: 'sent' });
            return of(true);
          }),
          catchError((error) => {
            update(photo.key, { state: 'failed', error: apiError(error) });
            return of(false);
          }),
        );
      }),
      toArray(),
    );
  }
  private session = inject(UserSession);
  roles = signal<{ condominioId: string; papel: string; ativo: boolean }[]>([]);
  get manager() {
    return this.roles().some(
      (r) =>
        r.condominioId === this.condoId &&
        r.ativo &&
        ['SINDICO', 'ADMINISTRADOR'].includes(r.papel),
    );
  }
  get canCreate() {
    return !['atas', 'avisos', 'manutencao'].includes(this.resource()) || this.manager;
  }
  get actionLabel() {
    return (
      this.action() ||
      (
        { atas: 'Nova ata', avisos: 'Novo comunicado', manutencao: 'Nova manutenção' } as Record<
          string,
          string
        >
      )[this.resource()] ||
      ''
    );
  }
  editingId = '';
  important = false;
  published = false;
  target = 'area';
  attachments = signal<Attachment[]>([]);
  history = signal<
    { id: string; statusNovo: string; comentario: string; usuarioNome: string; criadoEm: string }[]
  >([]);
  detailLoading = signal(false);
  status = 'EM_ANDAMENTO';
  comment = '';
  pendingDelete = false;
  label = category;
  date(value: string) {
    return new Date(value).toLocaleString('pt-BR');
  }
  owns(item: Item) {
    return item.data?.usuarioId === this.session.claims?.sub;
  }
  canAttach(item: Item) {
    return ['atas', 'manutencao'].includes(this.resource())
      ? this.manager
      : this.owns(item) || this.manager;
  }
  attachmentPath(item: Item) {
    return (
      '/' +
      (this.resource() === 'atas' ? 'ata' : this.resource()) +
      '/' +
      item.data?.id +
      (this.resource() === 'atas' ? '/arquivo' : '/arquivos')
    );
  }
  openDetails(item: Item) {
    this.selected.set(item);
    this.focusView();
    this.composing.set(false);
    this.attachments.set([]);
    this.history.set([]);
    this.pendingDelete = false;
    this.error.set('');
    this.comment = '';
    if (!item.data?.id) return;
    this.detailLoading.set(true);
    const requests: Record<string, import('rxjs').Observable<unknown>> = {};
    if (['chamado', 'atas', 'classificado', 'manutencao'].includes(this.resource()))
      requests['files'] = this.api.get<Attachment[]>(this.attachmentPath(item));
    if (this.resource() === 'chamado')
      requests['history'] = this.api.get('/chamado/' + item.data.id + '/historico');
    if (!Object.keys(requests).length) {
      this.detailLoading.set(false);
      return;
    }
    forkJoin(requests)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.detailLoading.set(false)),
      )
      .subscribe({
        next: (r) => {
          if (this.selected() !== item) return;
          this.attachments.set((r['files'] || []) as Attachment[]);
          this.history.set((r['history'] || []) as ReturnType<typeof this.history>);
        },
        error: (e) => this.error.set(apiError(e)),
      });
  }
  edit(item: Item) {
    this.openForm();
    this.editingId = item.data!.id!;
    this.draftTitle = item.title;
    this.draftDescription = item.description;
    this.draftCategory = category(item.data!.tipo);
    this.price = item.data!.preco ?? null;
    this.important = !!item.data!.importante;
  }
  change(action: string) {
    const item = this.selected();
    if (!item?.data?.id || this.saving()) return;
    const id = item.data.id,
      resource = this.resource();
    let url = '/' + resource + '/' + id + '/' + action;
    let body: unknown = {};
    if (resource === 'atas') url = '/api/condominios/' + this.condoId + '/atas/' + id + '/publicar';
    if (resource === 'chamado') body = { status: this.status, comentario: this.comment.trim() };
    if (resource === 'classificado') body = { status: 'ENCERRADO' };
    const request: import('rxjs').Observable<unknown> =
      action === 'excluir'
        ? this.api.remove('/condominio/' + this.condoId + '/avisos/' + id)
        : this.api.patch(url, body);
    this.saving.set(true);
    this.error.set('');
    request
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.load();
          this.feedback.set('Alteração salva com sucesso.');
        },
        error: (e) => this.error.set(apiError(e)),
      });
  }
  upload(event: Event) {
    const input = event.target as HTMLInputElement,
      file = input.files?.[0],
      item = this.selected();
    if (!file || !item || this.saving()) return;
    if (!file.size || file.size > 10 * 1024 * 1024) {
      this.error.set('Escolha um arquivo com conteúdo e até 10 MB.');
      input.value = '';
      return;
    }
    this.saving.set(true);
    this.error.set('');
    const type = (
      {
        atas: 'ATA',
        chamado: 'CHAMADO',
        classificado: 'CLASSIFICADO',
        manutencao: 'MANUTENCAO',
      } as Record<string, string>
    )[this.resource()];
    this.api
      .upload(this.condoId, type, file)
      .pipe(
        switchMap((a) => this.api.post(this.attachmentPath(item), { arquivoId: a.id })),
        takeUntilDestroyed(this.destroyRef),
        finalize(() => {
          this.saving.set(false);
          input.value = '';
        }),
      )
      .subscribe({
        next: () => {
          this.openDetails(item);
          this.feedback.set('Anexo enviado.');
        },
        error: (e) => this.error.set(apiError(e)),
      });
  }
  unlink(file: Attachment) {
    const item = this.selected();
    if (!item || this.saving()) return;
    this.saving.set(true);
    this.api
      .remove(this.attachmentPath(item) + '/' + file.id)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => this.attachments.update((a) => a.filter((f) => f.id !== file.id)),
        error: (e) => this.error.set(apiError(e)),
      });
  }
  download(file: Attachment) {
    if (this.downloading()) return;
    this.downloading.set(file.id);
    this.error.set('');
    this.api
      .get<{ url: string }>('/arquivo/' + file.arquivoId + '/download')
      .pipe(
        switchMap((r) => {
          if (!/^https?:\/\//i.test(r.url)) throw new Error('invalid-download');
          return this.api.blob(r.url);
        }),
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.downloading.set('')),
      )
      .subscribe({
        next: (blob) => {
          const url = URL.createObjectURL(blob);
          this.downloadUrls.add(url);
          const a = document.createElement('a');
          a.href = url;
          a.download = file.nomeOriginal;
          a.click();
          this.feedback.set('Download iniciado. Confira os arquivos baixados no seu dispositivo.');
          setTimeout(() => {
            URL.revokeObjectURL(url);
            this.downloadUrls.delete(url);
          }, 30000);
        },
        error: (e) => this.error.set(apiError(e)),
      });
  }
  private api = inject(PortalApi);
  private destroyRef = inject(DestroyRef);
  resource = input('');
  serverItems = signal<Item[]>([]);
  condos = signal<Condo[]>([]);
  condoId = '';
  loading = signal(false);
  saving = signal(false);
  error = signal('');
  options = signal<Option[]>([]);
  optionId = '';
  price: number | null = null;
  startTime = '';
  endTime = '';
  readonly formCategories = computed(() =>
    this.resource() === 'manutencao'
      ? ['PREVENTIVA', 'CORRETIVA']
      : this.resource() === 'chamado'
        ? ['Elétrica', 'Hidráulica', 'Limpeza', 'Outros']
        : this.categories(),
  );
  ngOnInit() {
    if (!this.resource()) return;
    this.loading.set(true);
    this.api
      .get<{ condominioId: string; papel: string; ativo: boolean }[]>('/papel-condominio/meus')
      .pipe(
        switchMap((roles) => {
          this.roles.set(roles);
          return this.api.condos();
        }),
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (rows) => {
          this.condos.set(rows);
          this.condoId = this.api.selectedCondo(rows);
          if (this.condoId) this.load();
          else this.loading.set(false);
        },
        error: (e) => {
          this.error.set(apiError(e));
          this.loading.set(false);
        },
      });
  }
  load() {
    this.clearPhotos();
    this.savedTicket.set(null);
    this.api.selectCondo(this.condoId);
    this.loading.set(true);
    this.error.set('');
    this.serverItems.set([]);
    this.selected.set(null);
    this.composing.set(false);
    this.feedback.set('');
    this.query.set('');
    this.filter.set('Todos');
    this.options.set([]);
    this.api
      .list(this.resource(), this.condoId)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (r) => this.serverItems.set(r.map((i) => this.toItem(i))),
        error: (e) => this.error.set(apiError(e)),
      });
  }
  private toItem(i: RecordData): Item {
    return {
      data: i,
      title: i.titulo || i.areaNome || 'Reserva de espaço',
      description:
        i.descricao || i.conteudo || i.resumo || i.observacao || 'Sem descrição adicional.',
      category: i.importante
        ? 'Importante'
        : this.resource() === 'atas'
          ? i.publicada
            ? 'Publicada'
            : 'Rascunho'
          : category(
              ['chamado', 'manutencao'].includes(this.resource())
                ? i.status
                : i.tipo || i.status || i.categoria,
            ),
      meta:
        i.preco != null
          ? i.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
          : i.inicio
            ? new Date(i.inicio).toLocaleString('pt-BR') +
              (i.fim ? ' até ' + new Date(i.fim).toLocaleString('pt-BR') : '')
            : i.criadoEm
              ? new Date(i.criadoEm).toLocaleDateString('pt-BR')
              : '',
    };
  }
  private saveRemote() {
    if (this.savedTicket()) {
      this.retryPhotos();
      return;
    }
    let body: unknown;
    const resource = this.resource();
    if (resource === 'chamado') {
      if (!this.optionId) {
        this.error.set('Selecione uma unidade.');
        return;
      }
      body = {
        unidadeId: this.optionId,
        titulo: this.draftTitle.trim(),
        descricao: this.draftDescription.trim(),
        categoria:
          (
            { Elétrica: 'ELETRICA', Hidráulica: 'HIDRAULICA', Limpeza: 'LIMPEZA' } as Record<
              string,
              string
            >
          )[this.draftCategory] || 'OUTROS',
      };
    } else if (resource === 'classificado') {
      if (this.draftCategory === 'Venda' && (this.price === null || this.price < 0)) {
        this.error.set('Informe o preço de venda.');
        return;
      }
      body = {
        condominioId: this.condoId,
        titulo: this.draftTitle.trim(),
        descricao: this.draftDescription.trim(),
        tipo: ({ Venda: 'VENDA', Doação: 'DOACAO', Serviço: 'SERVICO' } as Record<string, string>)[
          this.draftCategory
        ],
        preco: this.draftCategory === 'Doação' ? null : this.price,
      };
    } else if (resource === 'agendamento-espaco') {
      if (!this.optionId || !this.startTime || !this.endTime) {
        this.error.set('Selecione o espaço e os horários.');
        return;
      }
      const inicio = new Date(this.draftDate + 'T' + this.startTime),
        fim = new Date(this.draftDate + 'T' + this.endTime);
      if (fim <= inicio || inicio <= new Date()) {
        this.error.set('Escolha um período futuro com término após o início.');
        return;
      }
      body = {
        espacoComumId: this.optionId,
        inicio: inicio.toISOString(),
        fim: fim.toISOString(),
        observacao: this.draftDescription.trim(),
      };
    } else if (resource === 'atas') {
      if (!this.draftDate) {
        this.error.set('Informe a data da reunião.');
        return;
      }
      body = {
        titulo: this.draftTitle.trim(),
        resumo: this.draftDescription.trim(),
        dataReuniao: this.draftDate,
        publicada: this.published,
      };
    } else if (resource === 'avisos') {
      if (!this.editingId && !this.optionId) {
        this.error.set('Selecione o bloco.');
        return;
      }
      body = {
        blocoId: this.optionId,
        titulo: this.draftTitle.trim(),
        conteudo: this.draftDescription.trim(),
        importante: this.important,
      };
    } else if (resource === 'manutencao') {
      if (!this.optionId) {
        this.error.set('Selecione a área ou unidade.');
        return;
      }
      body = {
        condominioId: this.condoId,
        titulo: this.draftTitle.trim(),
        descricao: this.draftDescription.trim(),
        tipo: this.draftCategory,
        [this.target === 'area' ? 'areaId' : 'unidadeId']: this.optionId,
      };
    } else return;
    this.saving.set(true);
    this.error.set('');
    const path =
      resource === 'atas'
        ? '/api/condominios/' + this.condoId + '/atas'
        : resource === 'avisos'
          ? '/condominio/' + this.condoId + '/avisos'
          : '/' + resource;
    const request = this.editingId
      ? resource === 'avisos'
        ? this.api.put(path + '/' + this.editingId, body)
        : this.api.patch(path + '/' + this.editingId, body)
      : this.api.post(path, body);
    request
      .pipe(
        switchMap((r) => {
          this.serverItems.update((rows) =>
            this.editingId
              ? rows.map((i) => (i.data?.id === this.editingId ? this.toItem(r) : i))
              : [this.toItem(r), ...rows],
          );
          if (resource === 'chamado') {
            this.savedTicket.set(r);
            return this.sendPhotos(r).pipe(switchMap(() => of(r)));
          }
          return of(r);
        }),
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: (r) => {
          if (this.photos().some((p) => p.state === 'failed')) {
            this.feedback.set('Seu chamado já foi aberto.');
            this.error.set(
              'Algumas fotos não foram anexadas. Tente reenviar abaixo; o chamado não será criado novamente.',
            );
            return;
          }
          this.composing.set(false);
          this.clearPhotos();
          this.savedTicket.set(null);
          this.query.set('');
          this.filter.set('Todos');
          this.feedback.set(
            resource === 'chamado'
              ? 'Chamado aberto! Você pode acompanhar as atualizações nos detalhes.'
              : 'Tudo certo! Seu registro foi salvo.',
          );
        },
        error: (e) => this.error.set(apiError(e)),
      });
  }
  private retryPhotos() {
    const record = this.savedTicket();
    if (!record) return;
    this.saving.set(true);
    this.error.set('');
    this.sendPhotos(record)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false)),
      )
      .subscribe(() => {
        if (this.photos().some((p) => p.state === 'failed'))
          this.error.set(
            'O chamado está salvo, mas algumas fotos ainda não foram anexadas. Você pode tentar de novo ou continuar sem elas.',
          );
        else {
          this.composing.set(false);
          this.clearPhotos();
          this.savedTicket.set(null);
          this.feedback.set('Pronto! Todas as fotos foram anexadas ao seu chamado.');
        }
      });
  }

  title = input.required<string>();
  description = input.required<string>();
  action = input('');
  categories = input<string[]>([]);
  items = input<Item[]>([]);
  reservation = input(false);
  query = signal('');
  filter = signal('Todos');
  added = signal<Item[]>([]);
  selected = signal<Item | null>(null);
  composing = signal(false);
  feedback = signal('');
  draftTitle = '';
  draftDescription = '';
  draftCategory = '';
  draftDate = '';
  visible = computed(() =>
    (this.resource() ? this.serverItems() : [...this.added(), ...this.items()]).filter(
      (i) =>
        (this.filter() === 'Todos' || i.category === this.filter()) &&
        (i.title + ' ' + i.description)
          .toLocaleLowerCase('pt-BR')
          .includes(this.query().toLocaleLowerCase('pt-BR')),
    ),
  );
  openForm() {
    this.clearPhotos();
    this.savedTicket.set(null);
    this.feedback.set('');
    this.editingId = '';
    this.important = false;
    this.published = false;
    this.target = 'area';
    this.draftTitle = this.reservation() ? 'Reserva de espaço' : '';
    this.draftDescription = '';
    this.draftDate = '';
    this.draftCategory = this.formCategories()[0] || '';
    this.optionId = '';
    this.price = null;
    this.startTime = '';
    this.endTime = '';
    this.error.set('');
    if (this.resource() === 'avisos' || this.resource() === 'manutencao') this.loadTargets();
    if (this.resource() === 'chamado')
      this.api
        .units(this.condoId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (o) => {
            this.options.set(o);
            this.optionId = o[0]?.id || '';
          },
          error: (e) => this.error.set(apiError(e)),
        });
    if (this.resource() === 'agendamento-espaco')
      forkJoin({ spaces: this.api.spaces(this.condoId), areas: this.api.areas(this.condoId) })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: ({ spaces, areas }) => {
            const options = spaces
              .filter((s) => s.ativo)
              .map((s) => ({
                id: s.id,
                nome: areas.find((a) => a.id === s.areaId)?.nome || 'Espaço comum',
              }));
            this.options.set(options);
            this.optionId = options[0]?.id || '';
          },
          error: (e) => this.error.set(apiError(e)),
        });
    this.composing.set(true);
    this.focusView();
    this.selected.set(null);
  }
  loadTargets() {
    this.options.set([]);
    this.optionId = '';
    const url =
      '/' +
      (this.resource() === 'avisos' ? 'bloco' : this.target === 'area' ? 'area' : 'unidade') +
      '/condominio/' +
      this.condoId;
    this.api
      .get<{ id: string; nome?: string; numero?: string }[]>(url)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (rows) =>
          this.options.set(rows.map((r) => ({ id: r.id, nome: r.nome || 'Unidade ' + r.numero }))),
        error: (e) => this.error.set(apiError(e)),
      });
  }
  save(form?: NgForm) {
    if (this.saving()) return;
    if (!this.savedTicket() && form?.invalid) {
      form.control.markAllAsTouched();
      this.error.set('Falta pouco: revise os campos destacados para enviar.');
      return;
    }
    if (
      !this.draftTitle.trim() ||
      !this.draftDescription.trim() ||
      (this.reservation() && !this.draftDate)
    ) {
      this.error.set(
        'Preencha o título e a descrição' +
          (this.reservation() ? ', além da data da reserva.' : ' para continuar.'),
      );
      return;
    }
    if (this.resource()) {
      this.saveRemote();
      return;
    }
    this.added.update((a) => [
      {
        title: this.draftTitle.trim(),
        description: this.draftDescription.trim(),
        category: this.draftCategory,
        meta: this.draftDate
          ? 'Reserva para ' + this.draftDate.split('-').reverse().join('/')
          : 'Adicionado agora · Você',
      },
      ...a,
    ]);
    this.composing.set(false);
    this.query.set('');
    this.filter.set('Todos');
    this.feedback.set('Registro adicionado à demonstração.');
  }
}
