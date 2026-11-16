import { HttpClient } from '@angular/common/http';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UserSession } from '../../auth/user-session';
import { apiError } from '../../services/api-error';
import { Component, ChangeDetectorRef, inject, OnInit, DestroyRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-configuracoes',
  imports: [FormsModule],
  templateUrl: './configuracoes.html',
  styleUrl: './configuracoes.css',
})
export class ConfiguracoesPage implements OnInit {
  private cd = inject(ChangeDetectorRef);
  private http = inject(HttpClient);
  private session = inject(UserSession);
  private destroyRef = inject(DestroyRef);
  error = '';
  ngOnInit() {
    const id = this.session.claims?.sub;
    if (id)
      this.http
        .get<{ nome: string; email: string; telefone: string }>('/usuario/' + id)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (u) => {
            this.name = u.nome;
            this.email = u.email;
            this.phone = u.telefone || '';
            this.cd.markForCheck();
          },
          error: (e) => {
            this.error = apiError(e);
            this.cd.markForCheck();
          },
        });
    try {
      const p = JSON.parse(localStorage.getItem('eocape-preferences-' + id) || 'null');
      if (p) {
        this.notices = p.notices;
        this.reservations = p.reservations;
      }
    } catch {}
  }

  name = '';
  email = '';
  phone = '';
  notices = true;
  reservations = true;
  message = '';
  save() {
    localStorage.setItem(
      'eocape-preferences-' + this.session.claims?.sub,
      JSON.stringify({ notices: this.notices, reservations: this.reservations }),
    );
    this.message = 'Preferências salvas neste navegador.';
  }
}
