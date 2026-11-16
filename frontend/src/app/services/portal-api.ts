import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
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
}
export interface Option {
  id: string;
  nome: string;
}
@Injectable({ providedIn: 'root' })
export class PortalApi {
  private http = inject(HttpClient);
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
    return this.http
      .get<RecordData[] | { content: RecordData[] }>(url)
      .pipe(map((r) => (Array.isArray(r) ? r : r.content)));
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
  };
  return value ? labels[value] || value : 'Geral';
}
