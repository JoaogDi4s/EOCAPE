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

  erroEnvio = signal<string | null>(null);

  form = this.fb.group({
    nome: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(100)]),
    email: this.fb.nonNullable.control('', [
      Validators.required,
      Validators.pattern(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/),
      Validators.maxLength(100),
    ]),
    telefone: this.fb.control<string | null>(null, [Validators.pattern(/^[0-9]{10,15}$/)]),
    nome_condominio: this.fb.control<string | null>(null, [Validators.maxLength(150)]),
    perfil: this.fb.nonNullable.control<Perfil | ''>('', Validators.required),
    consentimento_contato: this.fb.nonNullable.control(false, Validators.requiredTrue),
  });

  salvar() {
    this.erroEnvio.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.http.post<Lead>('/lead', this.form.getRawValue()).subscribe({
      next: (lead) => {
        console.log('Lead adicionado com sucesso!', lead.id);
        this.form.reset();
      },
      error: (err) => {
        console.error('Erro ao salvar', err);
        this.erroEnvio.set(
          err.status === 400
            ? 'Verifique os dados informados.'
            : 'Não foi possível salvar. Tente novamente.'
        );
      },
    });
  }
}