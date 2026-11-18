import { Icon } from '../../components/icon/icon';
import { ThemeToggle } from '../../components/theme-toggle/theme-toggle';
import { HttpClient } from '@angular/common/http';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PortalApi } from '../../services/portal-api';
import { apiError } from '../../services/api-error';
import { Component, ChangeDetectorRef, inject, OnInit, DestroyRef } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { ResponsiveMenu } from '../../components/navbar/responsive-menu';
import { UserSession } from '../../auth/user-session';

@Component({
  imports: [Icon, ThemeToggle, RouterOutlet, RouterLink, RouterLinkActive, ResponsiveMenu],
  selector: 'app-user-page',
  styleUrl: './user-page.css',
  templateUrl: './user-page.html',
})
export class UserPage implements OnInit {
  private cd = inject(ChangeDetectorRef);
  private http = inject(HttpClient);
  private api = inject(PortalApi);
  private destroyRef = inject(DestroyRef);
  userName = 'Meu perfil';
  noticeTitle = 'Acompanhe os comunicados';
  noticeDescription = 'Veja as atualizações da administração.';
  noticeError = '';
  isManager = false;
  ngOnInit() {
    this.api
      .roles()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (roles) => {
          this.isManager = roles.some(
            (r) => r.ativo && ['SINDICO', 'ADMINISTRADOR'].includes(r.papel),
          );
          this.cd.markForCheck();
        },
        error: () => {
          this.isManager = false;
          this.cd.markForCheck();
        },
      });
    const id = this.userSession.claims?.sub;
    if (!id) return;
    this.http
      .get<{ nome: string }>('/usuario/' + id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (u) => {
          this.userName = u.nome;
          this.cd.markForCheck();
        },
        error: () => {
          this.userName = 'Meu perfil';
          this.cd.markForCheck();
        },
      });
    this.api
      .condos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (cs) => {
          if (!cs.length) {
            this.noticeDescription = 'Seu acesso ainda não possui um condomínio vinculado.';
            this.cd.markForCheck();
            return;
          }
          this.api
            .list('avisos', cs[0].id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
              next: (rows) => {
                const first = rows[0];
                if (first) {
                  this.noticeTitle = first.titulo || 'Comunicado';
                  this.noticeDescription = first.conteudo || '';
                } else this.noticeDescription = 'Nenhum comunicado publicado no momento.';
                this.cd.markForCheck();
              },
              error: (e) => {
                this.noticeError = apiError(e);
                this.cd.markForCheck();
              },
            });
        },
        error: (e) => {
          this.noticeError = apiError(e);
          this.cd.markForCheck();
        },
      });
  }

  private readonly router = inject(Router);
  private readonly userSession = inject(UserSession);

  get isChildPage(): boolean {
    return this.router.url.split('?')[0].startsWith('/user-page/');
  }

  private section: 'home' | 'configuracoes' | 'sobre' = 'home';
  get activeSection(): 'home' | 'configuracoes' | 'sobre' {
    return this.router.url.includes('/user-page/configuracoes')
      ? 'configuracoes'
      : this.isChildPage
        ? 'home'
        : this.section;
  }
  set activeSection(value: 'home' | 'configuracoes' | 'sobre') {
    this.section = value;
  }
  selectedService: string | null = null;
  private previousSection: 'home' | 'configuracoes' | 'sobre' = 'home';

  selectSection(section: 'home' | 'configuracoes' | 'sobre'): void {
    this.selectedService = null;
    this.activeSection = section;
    this.router.navigate([section === 'configuracoes' ? '/user-page/configuracoes' : '/user-page']);
  }

  openService(service: string): void {
    this.previousSection = this.activeSection;
    this.selectedService = service;
    const paths: Record<string, string> = {
      Relatos: 'relatos',
      Classificados: 'classificados',
      Atas: 'atas',
      Sugestões: 'sugestoes',
      Encomendas: 'encomendas',
      Comunicados: 'comunicados',
      Reservas: 'reservas',
      Manutenções: 'manutencoes',
    };
    this.router.navigate(['/user-page', paths[service]]);
  }

  returnToPreviousContent(): void {
    this.selectedService = null;
    this.activeSection = this.previousSection;
  }

  sair(): void {
    this.userSession.end();
    this.router.navigate(['/login'], { replaceUrl: true });
  }
}
