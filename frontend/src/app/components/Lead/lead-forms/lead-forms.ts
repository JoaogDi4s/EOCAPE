import { finalize } from 'rxjs';
import { apiError } from '../../../services/api-error';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Lead, Perfil } from '../Lead';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-lead-forms',
  templateUrl: './lead-forms.html',
})
export class NewMemberForm {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);

  enviando = signal(false);
  sucesso = signal(false);
  erroEnvio = signal<string | null>(null);

  form = this.fb.group({
    nome: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(100)]),
    email: this.fb.nonNullable.control('', [
      Validators.required,
      Validators.pattern(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/),
      Validators.maxLength(100),
    ]),
    telefone: this.fb.control<string | null>(null, [
      Validators.required,
      Validators.pattern(/^[0-9]{10,15}$/),
    ]),
    nomeCondominio: this.fb.control<string | null>(null, [
      Validators.required,
      Validators.maxLength(150),
    ]),
    perfil: this.fb.nonNullable.control<Perfil | ''>('', Validators.required),
    consentimentoContato: this.fb.nonNullable.control(false, Validators.requiredTrue),
  });

  salvar() {
    if (this.enviando()) return;
    this.erroEnvio.set(null);
    this.sucesso.set(false);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.http
      .post<Lead>('/lead', this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: (lead) => {
          this.sucesso.set(true);
          this.form.reset();
        },
        error: (err) => {
          this.erroEnvio.set(apiError(err));
        },
      });
  }
}
