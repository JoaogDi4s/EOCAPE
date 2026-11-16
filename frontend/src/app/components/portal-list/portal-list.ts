import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { forkJoin, finalize } from 'rxjs';
import { PortalApi, Condo, Option, RecordData, category } from '../../services/portal-api';
import { apiError } from '../../services/api-error';
import { Component, input, signal, computed, inject, OnInit, DestroyRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
export interface Item {
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
    this.resource() === 'chamado'
      ? ['Elétrica', 'Hidráulica', 'Limpeza', 'Outros']
      : this.categories(),
  );
  ngOnInit() {
    if (!this.resource()) return;
    this.loading.set(true);
    this.api
      .condos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (rows) => {
          this.condos.set(rows);
          this.condoId = rows[0]?.id || '';
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
      title: i.titulo || i.areaNome || 'Reserva de espaço',
      description:
        i.descricao || i.conteudo || i.resumo || i.observacao || 'Sem descrição adicional.',
      category: i.importante
        ? 'Importante'
        : this.resource() === 'atas'
          ? i.publicada
            ? 'Publicada'
            : 'Rascunho'
          : category(i.tipo || i.status || i.categoria),
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
    } else return;
    this.saving.set(true);
    this.error.set('');
    this.api
      .create(resource, body)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: (r) => {
          this.serverItems.update((a) => [this.toItem(r), ...a]);
          this.composing.set(false);
          this.query.set('');
          this.filter.set('Todos');
          this.feedback.set('Registro enviado com sucesso.');
        },
        error: (e) => this.error.set(apiError(e)),
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
    this.draftTitle = this.reservation() ? 'Reserva de espaço' : '';
    this.draftDescription = '';
    this.draftDate = '';
    this.draftCategory = this.formCategories()[0];
    this.optionId = '';
    this.price = null;
    this.startTime = '';
    this.endTime = '';
    this.error.set('');
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
    this.selected.set(null);
  }
  save() {
    if (this.saving()) return;
    if (
      !this.draftTitle.trim() ||
      !this.draftDescription.trim() ||
      (this.reservation() && !this.draftDate)
    )
      return;
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
