import { Component, ChangeDetectorRef, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { UserSession } from '../../auth/user-session';
import { apiError } from '../../services/api-error';
@Component({
  imports: [FormsModule],
  selector: 'app-login',
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private cd = inject(ChangeDetectorRef);
  private router = inject(Router);
  private http = inject(HttpClient);
  private userSession = inject(UserSession);
  senhaVisivel = false;
  mostrarAjuda = false;
  cpf = '';
  senha = '';
  busy = false;
  error = '';
  step: 'login' | 'email' | 'codigo' | 'senha' = 'login';
  email = '';
  codigo = '';
  novaSenha = '';
  confirmacaoSenha = '';
  continuar(event: Event) {
    event.preventDefault();
    if (this.busy) return;
    this.error = '';
    const cpf = this.cpf.replace(/\D/g, '');
    if (cpf.length !== 11 || !this.senha) {
      this.error = 'Informe um CPF com 11 dígitos e sua senha.';
      return;
    }
    this.busy = true;
    this.http
      .post<{ token: string; trocaSenhaObrigatoria: boolean }>('/usuario/login', {
        cpf,
        senha: this.senha,
      })
      .pipe(
        finalize(() => {
          this.busy = false;
          this.cd.markForCheck();
        }),
      )
      .subscribe({
        next: (r) => {
          this.userSession.start(r.token);
          this.senha = '';
          if (r.trocaSenhaObrigatoria) this.step = 'email';
          else void this.router.navigate(['/boas-vindas']);
        },
        error: (e) => (this.error = apiError(e)),
      });
  }
  primeiroAcesso() {
    if (this.busy) return;
    this.error = '';
    if (this.step === 'senha' && this.novaSenha !== this.confirmacaoSenha) {
      this.error = 'As senhas precisam ser iguais.';
      return;
    }
    this.busy = true;
    const url =
      this.step === 'email'
        ? '/verificacao-email/codigo'
        : this.step === 'codigo'
          ? '/verificacao-email/verificar'
          : '/usuario/definir-senha';
    const body =
      this.step === 'email'
        ? { email: this.email }
        : this.step === 'codigo'
          ? { email: this.email, codigo: this.codigo }
          : { novaSenha: this.novaSenha, confirmacaoSenha: this.confirmacaoSenha };
    this.http
      .post<{ token?: string; tokenDefinicaoSenha?: string }>(url, body)
      .pipe(
        finalize(() => {
          this.busy = false;
          this.cd.markForCheck();
        }),
      )
      .subscribe({
        next: (r) => {
          if (this.step === 'email') this.step = 'codigo';
          else if (this.step === 'codigo') {
            this.userSession.start(r.tokenDefinicaoSenha!);
            this.step = 'senha';
          } else {
            this.userSession.start(r.token!);
            this.novaSenha = '';
            this.confirmacaoSenha = '';
            void this.router.navigate(['/boas-vindas']);
          }
        },
        error: (e) => (this.error = apiError(e)),
      });
  }
  voltar() {
    this.userSession.end();
    this.step = 'login';
    this.error = '';
  }
}
