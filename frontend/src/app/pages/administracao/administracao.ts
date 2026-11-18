import { Icon } from '../../components/icon/icon';
import { Component, DestroyRef, computed, inject, signal, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { forkJoin, finalize, catchError, of } from 'rxjs';
import { PortalApi, Condo, Resident, RecordData, Role, category } from '../../services/portal-api';
import { apiError } from '../../services/api-error';

interface Structure {
  id: string;
  nome?: string;
  numero?: string;
  blocoId?: string;
  areaId?: string;
  ativo?: boolean;
  capacidade?: number;
  exigeAprovacao?: boolean;
}
@Component({
  selector: 'app-administracao',
  imports: [Icon, FormsModule, RouterLink],
  templateUrl: './administracao.html',
  styleUrls: ['../../components/portal-list/portal-list.css', './administracao.css'],
})
export class Administracao implements OnInit {
  private api = inject(PortalApi);
  private destroy = inject(DestroyRef);
  mode = inject(ActivatedRoute).snapshot.data['mode'] as 'dashboard' | 'moradores' | 'estrutura';
  condos = signal<Condo[]>([]);
  condoId = '';
  loading = signal(true);
  saving = signal(false);
  error = signal('');
  feedback = signal('');
  residents = signal<Resident[]>([]);
  query = signal('');
  accessFilter = signal('Todos');
  filtered = computed(() =>
    this.residents().filter(
      (r) =>
        [r.nome, r.email, r.telefone, ...r.unidades.map((u) => u.bloco + ' ' + u.numero)]
          .join(' ')
          .toLocaleLowerCase('pt-BR')
          .includes(this.query().toLocaleLowerCase('pt-BR')) &&
        (this.accessFilter() === 'Todos' ||
          (this.accessFilter() === 'Concluído') === r.primeiroAcessoConcluido),
    ),
  );
  blocks = signal<Structure[]>([]);
  units = signal<Structure[]>([]);
  areas = signal<Structure[]>([]);
  spaces = signal<Structure[]>([]);
  roles = signal<Role[]>([]);
  tickets = signal<RecordData[]>([]);
  reservations = signal<RecordData[]>([]);
  maintenance = signal<RecordData[]>([]);
  notices = signal<RecordData[]>([]);
  minutes = signal<RecordData[]>([]);
  ads = signal<RecordData[]>([]);
  label = category;
  pendingTickets = computed(() =>
    this.tickets().filter((r) => ['ABERTO', 'EM_ANDAMENTO'].includes(r.status || '')),
  );
  pendingReservations = computed(() => this.reservations().filter((r) => r.status === 'PENDENTE'));
  pendingMaintenance = computed(() =>
    this.maintenance().filter((r) => !['CONCLUIDA', 'CANCELADA'].includes(r.status || '')),
  );
  resolved = computed(() => this.tickets().filter((r) => r.status === 'RESOLVIDO').length);
  get minutesPublished() {
    return this.minutes().filter((r) => r.publicada).length;
  }
  get activeAds() {
    return this.ads().filter((r) => r.status === 'ATIVO').length;
  }
  resolutionRate = computed(() =>
    this.tickets().length ? Math.round((this.resolved() / this.tickets().length) * 100) : 0,
  );
  unreadAccess = computed(() => this.residents().filter((r) => !r.primeiroAcessoConcluido).length);
  unavailable = signal<string[]>([]);
  readonly statuses = ['ABERTO', 'EM_ANDAMENTO', 'RESOLVIDO', 'CANCELADO'];
  count(status: string) {
    return this.tickets().filter((t) => t.status === status).length;
  }
  percent(status: string) {
    return this.tickets().length ? (this.count(status) / this.tickets().length) * 100 : 0;
  }
  name = '';
  existingResident = false;
  residentType = 'INQUILINO';
  email = '';
  phone = '';
  cpf = '';
  blockNumber: number | null = null;
  apartment: number | null = null;
  unitId = '';
  composing = false;
  structureType = 'bloco';
  structureName = '';
  description = '';
  parentId = '';
  floor = 0;
  capacity = 1;
  fee = 0;
  opening = '';
  closing = '';
  approval = true;
  roleUser = '';
  roleName = 'ADMINISTRADOR';
  revokeId = '';
  ngOnInit() {
    forkJoin({ condos: this.api.condos(), roles: this.api.roles() })
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe({
        next: ({ condos, roles }) => {
          this.condos.set(
            condos.filter((c) =>
              roles.some(
                (r) =>
                  r.condominioId === c.id &&
                  r.ativo &&
                  ['SINDICO', 'ADMINISTRADOR'].includes(r.papel),
              ),
            ),
          );
          this.condoId = this.api.selectedCondo(this.condos());
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
    this.api.selectCondo(this.condoId);
    this.loading.set(true);
    this.error.set('');
    this.feedback.set('');
    this.composing = false;
    this.revokeId = '';
    this.unavailable.set([]);
    this.query.set('');
    this.accessFilter.set('Todos');
    this.residents.set([]);
    this.blocks.set([]);
    this.units.set([]);
    this.areas.set([]);
    this.spaces.set([]);
    this.roles.set([]);
    this.tickets.set([]);
    this.reservations.set([]);
    this.maintenance.set([]);
    this.notices.set([]);
    this.minutes.set([]);
    this.ads.set([]);
    const id = this.condoId;
    if (this.mode === 'dashboard') {
      const safe = <T>(name: string, request: import('rxjs').Observable<T[]>) =>
        request.pipe(
          catchError(() => {
            this.unavailable.update((a) => [...a, name]);
            return of([] as T[]);
          }),
        );
      forkJoin({
        residents: safe('Moradores', this.api.residents(id)),
        tickets: safe('Relatos', this.api.list('chamado', id)),
        reservations: safe('Reservas', this.api.list('agendamento-espaco', id)),
        maintenance: safe('Manutenções', this.api.list('manutencao', id)),
        minutes: safe('Atas', this.api.list('atas', id)),
        notices: safe('Comunicados', this.api.list('avisos', id)),
        ads: safe('Classificados', this.api.list('classificado', id)),
      })
        .pipe(
          takeUntilDestroyed(this.destroy),
          finalize(() => this.loading.set(false)),
        )
        .subscribe((r) => {
          this.residents.set(r.residents);
          this.tickets.set(r.tickets);
          this.reservations.set(r.reservations);
          this.maintenance.set(r.maintenance);
          this.minutes.set(r.minutes);
          this.notices.set(r.notices);
          this.ads.set(r.ads);
        });
    } else {
      forkJoin({
        residents: this.api.residents(id),
        blocks: this.api.get<Structure[]>('/bloco/condominio/' + id),
        units: this.api.get<Structure[]>('/unidade/condominio/' + id),
        areas: this.api.get<Structure[]>('/area/condominio/' + id),
        spaces: this.api.get<Structure[]>('/espaco-comum/condominio/' + id),
        roles: this.api.get<Role[]>('/papel-condominio/condominio/' + id),
      })
        .pipe(
          takeUntilDestroyed(this.destroy),
          finalize(() => this.loading.set(false)),
        )
        .subscribe({
          next: (r) => {
            this.residents.set(r.residents);
            this.blocks.set(r.blocks);
            this.units.set(r.units);
            this.areas.set(r.areas);
            this.spaces.set(r.spaces);
            this.roles.set(r.roles);
          },
          error: (e) => this.error.set(apiError(e)),
        });
    }
  }
  unavailableMetric(name: string) {
    return this.loading() || this.unavailable().includes(name);
  }
  createResident(form?: NgForm) {
    if (form?.invalid) {
      form.control.markAllAsTouched();
      this.error.set(
        'Confira os campos destacados e preencha as informações obrigatórias antes de continuar.',
      );
      return;
    }
    if (this.saving() || !this.unitId) return;
    this.saving.set(true);
    this.error.set('');
    const existing = this.existingResident;
    const body = existing
      ? {
          cpf: this.cpf.replace(/\D/g, ''),
          email: this.email.trim(),
          unidadeId: this.unitId,
          tipoVinculo: this.residentType,
        }
      : {
          tipoVinculo: this.residentType,
          unidadeId: this.unitId,
          usuario: {
            nome: this.name.trim(),
            cpf: this.cpf.replace(/\D/g, ''),
            email: this.email.trim(),
            telefone: this.phone.replace(/\D/g, ''),
            bloco: this.blockNumber,
            apartamento: this.apartment,
          },
        };
    this.api
      .post('/condominio/' + this.condoId + '/moradores' + (existing ? '/vincular' : ''), body)
      .pipe(
        takeUntilDestroyed(this.destroy),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.name = '';
          this.cpf = '';
          this.email = '';
          this.phone = '';
          this.load();
          this.feedback.set(
            existing
              ? 'Cadastro existente vinculado à unidade. A senha e os dados do morador foram preservados.'
              : 'Morador cadastrado e vinculado à unidade. A senha inicial segue o padrão B{bloco}AP{apartamento} informado no cadastro.',
          );
        },
        error: (e) => this.error.set(apiError(e)),
      });
  }
  unitName(u: Structure) {
    return (
      (this.blocks().find((b) => b.id === u.blocoId)?.nome || 'Bloco') + ' · Unidade ' + u.numero
    );
  }
  areaName(id?: string) {
    return this.areas().find((a) => a.id === id)?.nome || 'Área comum';
  }
  userName(id: string) {
    return this.residents().find((r) => r.id === id)?.nome || 'Gestor sem vínculo residencial';
  }
  createStructure() {
    if (this.saving()) return;
    const type = this.structureType;
    let body: unknown;
    if (type === 'bloco') body = { nome: this.structureName.trim(), condominioId: this.condoId };
    else if (type === 'unidade')
      body = { numero: this.structureName.trim(), andar: this.floor, blocoId: this.parentId };
    else if (type === 'area')
      body = {
        nome: this.structureName.trim(),
        descricao: this.description.trim(),
        condominioId: this.condoId,
        ativo: true,
      };
    else {
      if (this.opening && this.closing && this.closing <= this.opening) {
        this.error.set('O fechamento deve ser posterior à abertura.');
        return;
      }
      body = {
        areaId: this.parentId,
        capacidade: this.capacity,
        taxa: this.fee,
        regras: this.description.trim(),
        horarioAbertura: this.opening || null,
        horarioFechamento: this.closing || null,
        exigeAprovacao: this.approval,
        ativo: true,
      };
    }
    this.saving.set(true);
    this.error.set('');
    this.api
      .post('/' + type, body)
      .pipe(
        takeUntilDestroyed(this.destroy),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.structureName = '';
          this.description = '';
          this.parentId = '';
          this.load();
          this.feedback.set('Estrutura cadastrada com sucesso.');
        },
        error: (e) => this.error.set(apiError(e)),
      });
  }
  grantRole() {
    if (!this.roleUser || this.saving()) return;
    this.saving.set(true);
    this.error.set('');
    this.api
      .post('/papel-condominio', {
        condominioId: this.condoId,
        usuarioId: this.roleUser,
        papel: this.roleName,
        ativo: true,
      })
      .pipe(
        takeUntilDestroyed(this.destroy),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: () => {
          this.load();
          this.feedback.set('Permissão atribuída.');
        },
        error: (e) => this.error.set(apiError(e)),
      });
  }
  revokeRole() {
    if (!this.revokeId || this.saving()) return;
    this.saving.set(true);
    this.error.set('');
    this.api
      .remove('/papel-condominio/' + this.revokeId)
      .pipe(
        takeUntilDestroyed(this.destroy),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({ next: () => this.ngOnInit(), error: (e) => this.error.set(apiError(e)) });
  }
}
