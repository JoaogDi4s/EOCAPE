import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, switchMap, forkJoin, of } from 'rxjs';
export interface Condo {
  id: string;
  nome: string;
}
export interface RecordData {
  id?: string;
  titulo?: string;
  descricao?: string;
  conteudo?: string;
  resumo?: string;
  categoria?: string;
  tipo?: string;
  status?: string;
  importante?: boolean;
  publicada?: boolean;
  criadoEm?: string;
  inicio?: string;
  fim?: string;
  dataReuniao?: string;
  areaNome?: string;
  observacao?: string;
  preco?: number;
  usuarioId?: string;
  usuarioNome?: string;
  blocoId?: string;
}
export interface Role {
  id: string;
  condominioId: string;
  usuarioId: string;
  papel: string;
  ativo: boolean;
}
export interface Resident {
  id: string;
  nome: string;
  email: string;
  telefone: string;
  primeiroAcessoConcluido: boolean;
  unidades: { id: string; numero: string; bloco: string; tipoVinculo: string; ativo: boolean }[];
}
export interface Attachment {
  id: string;
  arquivoId: string;
  nomeOriginal: string;
}
export interface Option {
  id: string;
  nome: string;
}
@Injectable({ providedIn: 'root' })
export class PortalApi {
  private http = inject(HttpClient);
  selectedCondo(allowed: Condo[]) {
    const saved = sessionStorage.getItem('eocape-condominio');
    return allowed.find((c) => c.id === saved)?.id || allowed[0]?.id || '';
  }
  selectCondo(id: string) {
    sessionStorage.setItem('eocape-condominio', id);
  }
  roles() {
    return this.http.get<Role[]>('/papel-condominio/meus');
  }
  blob(url: string) {
    return this.http.get(url, { responseType: 'blob' });
  }
  residents(id: string) {
    return this.http.get<Resident[]>('/condominio/' + id + '/moradores');
  }
  get<T>(url: string) {
    return this.http.get<T>(url);
  }
  post<T = RecordData>(url: string, body: unknown) {
    return this.http.post<T>(url, body);
  }
  patch(url: string, body: unknown = {}) {
    return this.http.patch<RecordData>(url, body);
  }
  put(url: string, body: unknown) {
    return this.http.put<RecordData>(url, body);
  }
  remove(url: string) {
    return this.http.delete<void>(url);
  }
  upload(id: string, tipo: string, file: File) {
    const body = new FormData();
    body.append('arquivo', file);
    return this.http.post<{ id: string }>(
      '/arquivo/upload?condominioId=' + id + '&tipo=' + tipo,
      body,
    );
  }
  condos() {
    return this.http.get<Condo[]>('/condominio/meus');
  }
  list(resource: string, id: string): Observable<RecordData[]> {
    const url =
      resource === 'avisos'
        ? '/condominio/' + id + '/avisos?size=100'
        : resource === 'atas'
          ? '/api/condominios/' + id + '/atas'
          : '/' + resource + '/condominio/' + id;
    return this.http.get<RecordData[] | { content: RecordData[]; totalPages?: number }>(url).pipe(
      switchMap((r) => {
        if (Array.isArray(r)) return of(r);
        if (!r.totalPages || r.totalPages <= 1 || resource !== 'avisos') return of(r.content);
        return forkJoin(
          Array.from({ length: r.totalPages - 1 }, (_, index) =>
            this.http.get<{ content: RecordData[] }>(url + '&page=' + (index + 1)),
          ),
        ).pipe(map((pages) => [...r.content, ...pages.flatMap((page) => page.content)]));
      }),
    );
  }
  units(id: string) {
    return this.http
      .get<{ id: string; numero: string }[]>('/unidade/condominio/' + id)
      .pipe(map((rows) => rows.map((r) => ({ id: r.id, nome: 'Unidade ' + r.numero }))));
  }
  spaces(id: string) {
    return this.http.get<{ id: string; areaId: string; ativo: boolean }[]>(
      '/espaco-comum/condominio/' + id,
    );
  }
  areas(id: string) {
    return this.http.get<{ id: string; nome: string }[]>('/area/condominio/' + id);
  }
  create(resource: string, body: unknown) {
    return this.http.post<RecordData>('/' + resource, body);
  }
}
export function category(value?: string) {
  const labels: Record<string, string> = {
    CONFIRMADO: 'Confirmado',
    AGENDADA: 'Agendada',
    ABERTO: 'Aberto',
    EM_ANDAMENTO: 'Em andamento',
    RESOLVIDO: 'Resolvido',
    CANCELADO: 'Cancelado',
    CANCELADA: 'Cancelada',
    VENDA: 'Venda',
    DOACAO: 'Doação',
    SERVICO: 'Serviço',
    PENDENTE: 'Pendente',
    APROVADO: 'Aprovado',
    APROVADA: 'Aprovada',
    PROGRAMADA: 'Programada',
    CONCLUIDA: 'Concluída',
    CONCLUIDO: 'Concluído',
    RASCUNHO: 'Rascunho',
    PUBLICADA: 'Publicada',
    ATIVO: 'Ativo',
    ENCERRADO: 'Encerrado',
    PREVENTIVA: 'Preventiva',
    CORRETIVA: 'Corretiva',
  };
  return value ? labels[value] || value : 'Geral';
}
